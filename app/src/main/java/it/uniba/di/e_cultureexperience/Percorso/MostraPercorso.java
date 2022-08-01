package it.uniba.di.e_cultureexperience.Percorso;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.GestionePercorso;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettiDiInteresseAdapter;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.R;

public class MostraPercorso extends Activity {
    ArrayList<OggettoDiInteresse> oggetti_di_interesse;
    ListView list_view_oggetti;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.percorso);

        oggetti_di_interesse = new ArrayList<>();
        list_view_oggetti = findViewById(R.id.lista_oggetti);

        //prendo l' oggetto passato dall' intent
        Percorso percorso = getIntent().getExtras().getParcelable("percorso");
        //prendo i riferimenti degli elementi del layout
        TextView nome_percorso = findViewById(R.id.nome);
        TextView descrizione_percorso = findViewById(R.id.descrizione);
        TextView durata_percorso = findViewById(R.id.durata);
        Button button_avvia_percorso = (Button) findViewById(R.id.btn_avvia_percorso);

        nome_percorso.setText(percorso.getNome());
        descrizione_percorso.setText(percorso.getDescrizione());
        durata_percorso.setText(Integer.toString(percorso.getDurata()));

        //OGGETTI PER FIREBASE
        FirebaseFirestore db;
        DocumentReference docRef;
        db = FirebaseFirestore.getInstance();

        //bottone
        button_avvia_percorso.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //prendo gli oggetti relativi al percorso
                db.collection("/percorsi/"+percorso.getId()+"/oggetti")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> id_oggetti_da_trovare = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id_oggetto = document.getId();
                                Log.d("Endrit","oggetto trovato: "+id_oggetto);
                                id_oggetti_da_trovare.add(id_oggetto);
                            }
                            //setta gli id degli oggetti da raggiungere nel percorso
                            ((GestionePercorso) MostraPercorso.this.getApplication()).setId_oggetti_da_trovare(id_oggetti_da_trovare);
                            //setta come id_oggetto, qr_code e id_bluetooth dell' oggetto da trovare quelli del primo oggetto del percorso
                            ((GestionePercorso) MostraPercorso.this.getApplication()).setId_oggetto_da_trovare(id_oggetti_da_trovare.get(0));
                            //qr
                            //id_bluetooth
                            //fa partire la ricerca bluetooth del primo oggetto
                            //Porta sull' activity che mostra lo stato del percorso appena avviato
                            //Intent i = new Intent(context, MostraPercorso.class);
                            //i.putExtra("percorso", percorso);
                            //context.startActivity(i);
                        } else {
                            Log.w("ENDRIT", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                        }
                    }
                });
            }
        });

        //prendo gli oggetti di quel percorso

        //innanzitutto ricavo gli id degli oggetti del percorso
        ArrayList<String> id_oggetti = new ArrayList<>();

        db.collection("/percorsi/"+percorso.getId()+"/oggetti")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id_oggetto = document.getId();
                        Log.d("Endrit","oggetto trovato: "+id_oggetto);
                        id_oggetti.add(id_oggetto);
                    }
                } else {
                    Log.w("ENDRIT", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                }
            }
        });

        //ora prendo gli oggetti che si trovano tra gli id presi prima
        db.collection("oggetti")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id_oggetto = document.getId();
                        if(id_oggetti.contains(id_oggetto))
                        {
                            OggettoDiInteresse temp = document.toObject(OggettoDiInteresse.class);
                            temp.setId(document.getId());
                            oggetti_di_interesse.add(temp);
                        }
                    }
                    OggettiDiInteresseAdapter customAdapter = new OggettiDiInteresseAdapter(getApplicationContext(), oggetti_di_interesse);
                    list_view_oggetti.setAdapter(customAdapter);
                } else {
                    Log.w("ENDRIT", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                }
            }
        });
    }
}