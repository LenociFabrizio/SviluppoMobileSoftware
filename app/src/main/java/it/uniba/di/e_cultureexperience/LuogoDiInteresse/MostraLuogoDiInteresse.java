package it.uniba.di.e_cultureexperience.LuogoDiInteresse;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.e_cultureexperience.Accesso.FirstAccessActivity;
import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.Percorso.PercorsiAdapter;
import it.uniba.di.e_cultureexperience.Percorso.Percorso;
import it.uniba.di.e_cultureexperience.R;

public class MostraLuogoDiInteresse extends AppCompatActivity {
    private ArrayList<Percorso> percorsi;
    ListView list_view_percorsi;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    final String collectionPath = "luoghiPreferiti";
    private ToggleButton favorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.luogo_di_interesse);

        percorsi = new ArrayList<>();
        list_view_percorsi = findViewById(R.id.lista_percorsi);

        ImageView immagine = findViewById(R.id.immagine);
        TextView descrizioneLuogo = findViewById(R.id.descrizione);
        favorite = findViewById(R.id.favoriteToggleButton);

        //prendo l' oggetto passato dall' intent
        LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");

        isLuogoPreferito(luogo);

        Picasso.with(this)
                .load(luogo.getUrl_immagine())
                .into(immagine);
        descrizioneLuogo.setText(luogo.getDescrizione());
        setPercorsi(luogo);

        Toolbar mToolbar = findViewById(R.id.toolbar_luogodiinteresse);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(luogo.getNome());

        CollapsingToolbarLayout collapsingLayout = findViewById(R.id.collapsing_toolbar);
        collapsingLayout.setExpandedTitleColor(Color.parseColor("#ffffff"));
        collapsingLayout.setCollapsedTitleTextColor(Color.parseColor("#000000"));

        onCreateBottomNavigation();

    }

    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), DashboardMete.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_scan:
                    startActivity(new Intent(getApplicationContext(), FirstAccessActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }

            return false;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.secondary_top_menu, menu);
        return true;
    }

    public void setPercorsi(LuogoDiInteresse luogo){

        //OGGETTI PER FIREBASE
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //prendo i percorsi di quella meta
        db.collection("percorsi")
                .whereEqualTo("meta", luogo.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Percorso temp = document.toObject(Percorso.class);
                        temp.setId(document.getId());
                        percorsi.add(temp);
                    }
                    PercorsiAdapter customAdapter = new PercorsiAdapter(getApplicationContext(), percorsi);
                    list_view_percorsi.setAdapter(customAdapter);
                } else {
                    Log.w("ENDRIT", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                }
            }
        });
    }

    /**
     * Controllo se il luogo è già nei preferiti. Nel caso lo fosse, rendo il favoriteBtn colorato di rosso
     * @param luogo
     */
    public void isLuogoPreferito(LuogoDiInteresse luogo){

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        final int sizeDataBase = task.getResult().size();
                        if (sizeDataBase != 0) {
                            boolean luogoPreferito = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idUtenteDatabase = document.getString("idUtente");
                                String nomeLuogoDatabase = document.getString("nome");
                                //Posso aggiungere il luogoScelto solo se non è stato aggiunto precedentemente
                                if(idUtenteDatabase.equals(fAuth.getUid()) && nomeLuogoDatabase.equals(luogo.getNome())){
                                    favorite.setChecked(true);
                                    luogoPreferito = true;
                                }
                            }//fine for
                            if (!luogoPreferito){
                                favorite.setChecked(false);
                            }
                        }
                    }
                });
    }
    /**
     * Al click del favoriteButton richiama la funzione per inserire il determinato luogo di interesse come preferito o rimuoverlo(wip)
     * @param view
     */
    public void onFavoriteToggleClick(View view) {

        //prendo l' oggetto passato dall' intent
        LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");

        /**
         * se favoriteBtn ha il checked TRUE(sta già nei preferiti) allora se lo clicco elimino, altrimenti il contrario
         */
        if(favorite.isChecked()){
            db.collection(collectionPath)
                    .get()
                    .addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {

                            int sizeDataBase = task1.getResult().size();
                            if(sizeDataBase == 0){
                                scritturaLuogoDatabase(luogo, fAuth.getUid());
                                return;
                            }

                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                String idUtenteDatabase = document.getString("idUtente");
                                if(idUtenteDatabase.equals(fAuth.getUid())){
                                    scritturaLuogoDatabase(luogo, fAuth.getUid());
                                    Log.d("Scrittura luogoPreferito", "scrittura avvenuta, controlla nel db");
                                }
                            }
                        }else{
                            Log.e("Error", "Errore server metaPreferita.");
                        }
                    });
        }else{
            eliminazioneLuogoDatabase(luogo, fAuth.getUid());
        }

    }

    /**
     * Scrittura su database metaPreferita del luogoScelto
     * @param luogo
     * @param idUtente
     */
    public void scritturaLuogoDatabase(LuogoDiInteresse luogo, String idUtente)
    {
        Map<String, String> luogoScelto = new HashMap<>();
        luogoScelto.put("nome", luogo.getNome());
        luogoScelto.put("descrizione", luogo.getDescrizione());
        luogoScelto.put("idUtente", idUtente);
        luogoScelto.put("url_immagine", luogo.getUrl_immagine());

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                       final int sizeDataBase = task.getResult().size();
                       boolean luogoDuplicato = false;
                       if (sizeDataBase != 0) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               String idUtenteDatabase = document.getString("idUtente");
                               String nomeLuogoDatabase = document.getString("nome");
                               //Posso aggiungere il luogoScelto solo se non è stato aggiunto precedentemente
                               if(idUtenteDatabase.equals(fAuth.getUid()) && nomeLuogoDatabase.equals(luogoScelto.get("nome"))){
                                   luogoDuplicato = true;
                               }
                           }//fine for
                           if (!luogoDuplicato){
                               addDoc(collectionPath, luogoScelto);
                           }
                       }else{
                           addDoc(collectionPath, luogoScelto);
                       }
                   }
                });
    }

    /**
     * Permette di aggiungere un documento su firestore database
     * @param collectionPath
     * @param luogoScelto
     */
    public void addDoc(String collectionPath, Map<String, String> luogoScelto){
        db.collection(collectionPath)
                .add(luogoScelto)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

    public void eliminazioneLuogoDatabase(LuogoDiInteresse luogo, String idUtente)
    {
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        final int sizeDataBase = task.getResult().size();
                        if (sizeDataBase != 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idUtenteDatabase = document.getString("idUtente");
                                String nomeLuogoDatabase = document.getString("nome");
                                //Faccio coincidere l'id dell'utente loggato con quello del database
                                if(idUtenteDatabase.equals(idUtente) && nomeLuogoDatabase.equals(luogo.getNome())){
                                    deleteDoc(collectionPath, document.getId());
                                }
                            }
                        }
                    }else{
                        Log.e("Error", "Errore nella lettura del database metePreferite!");
                    }
                });
    }

    public void deleteDoc(String collectionPath, String documentReference)
    {
        db.collection(collectionPath).document(documentReference)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("Delete", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e ->
                        Log.w("Delete", "Error deleting document", e));
    }
}