package it.uniba.di.e_cultureexperience.LuogoDiInteresse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import it.uniba.di.e_cultureexperience.R;

public class MostraLuogoDiInteressePreferitoActivity extends AppCompatActivity {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String collectionPath = "luoghiPreferiti";
    RecyclerView list_view_mete;
    public LuoghiDiInteresseAdapter customAdapter;
    List<LuogoDiInteresse> listLuoghi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_luogo_di_interesse_preferito);

        leggiMetePreferita();

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
                                    //todo: Work in Progress, nel db c'è oggetto LuogoDiInteresse + idUtente (non c'è nella creazione dell'obj)
                                    String nomeLuogoDatabase = document.getString("nome");
                                    String descrizioneLuogoDatabase = document.getString("descrizione");
                                    String urlImmagineLuogoDatabase = document.getString("url_immagine");
                                    //TODO: Controllare creazione oggetto senza ID
                                    LuogoDiInteresse luogoDatabase = new LuogoDiInteresse(nomeLuogoDatabase, descrizioneLuogoDatabase, urlImmagineLuogoDatabase);
                                    //luogoDatabase.setId(document.getId());

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

}