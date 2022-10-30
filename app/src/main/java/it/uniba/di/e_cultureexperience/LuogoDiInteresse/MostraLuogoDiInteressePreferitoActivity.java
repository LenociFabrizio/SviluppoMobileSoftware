package it.uniba.di.e_cultureexperience.LuogoDiInteresse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.QrCodeScanner;
import it.uniba.di.e_cultureexperience.R;

public class MostraLuogoDiInteressePreferitoActivity extends AppCompatActivity {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String collectionPath = "luoghiPreferiti";
    RecyclerView list_view_mete;
    public LuoghiDiInteresseAdapter customAdapter;
    List<LuogoDiInteresse> listLuoghi;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_luogo_di_interesse_preferito);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        list_view_mete = findViewById(R.id.lista_luoghi);
        leggiMetePreferita();
        onCreateBottomNavigation();
    }

    /**
     * Legge dal database (metePreferite) le mete in base all'utente della sessione
     */
    public void leggiMetePreferita(){
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        final int sizeDataBase = task.getResult().size();

                        if (sizeDataBase != 0) {
                            listLuoghi = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idUtenteDatabase = document.getString("idUtente");
                                //Faccio coincidere l'id dell'utente loggato con quello del database
                                assert idUtenteDatabase != null;
                                if(idUtenteDatabase.equals(fAuth.getUid())){
                                    String nomeLuogoDatabase = document.getString("nome");
                                    String descrizioneLuogoDatabase = document.getString("descrizione");
                                    String urlImmagineLuogoDatabase = document.getString("url_immagine");
                                    LuogoDiInteresse luogoDatabase = new LuogoDiInteresse(nomeLuogoDatabase, descrizioneLuogoDatabase, urlImmagineLuogoDatabase);
                                    listLuoghi.add(luogoDatabase);
                                    customAdapter = new LuoghiDiInteresseAdapter(getApplicationContext(), listLuoghi);
                                    list_view_mete.setLayoutManager(new LinearLayoutManager(MostraLuogoDiInteressePreferitoActivity.this,LinearLayoutManager.VERTICAL,false));
                                    list_view_mete.setAdapter(customAdapter);
                                }
                            }
                        }
                    }else{
                        Log.e("Error", "Errore nella lettura del database luoghiPreferiti!");
                    }
                });
    }

    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.nav_home:
                    //entro nell'altra activity immettendo il segnalino appena caricato
                    startActivity(new Intent(getApplicationContext(), DashboardMeteActivity.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.nav_scan:
                    //entro nell'altra activity immettendo il segnalino appena caricato
                    startActivity(new Intent(getApplicationContext(), QrCodeScanner.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.nav_profile:

                    return true;
            }
            return false;
        });
    }

}