package it.uniba.di.e_cultureexperience.Percorso;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

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

        nome_percorso.setText(percorso.getNome());
        descrizione_percorso.setText(percorso.getDescrizione());
        durata_percorso.setText(Integer.toString(percorso.getDurata()));


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("oggetti").get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    String oggetiPercorsoDatabase = document.getString("idPercorso");

                    if(oggetiPercorsoDatabase.equals(percorso.getId())){
                        OggettoDiInteresse temp = document.toObject(OggettoDiInteresse.class);
                        oggetti_di_interesse.add(temp);
                    }
                }
                OggettiDiInteresseAdapter customAdapter = new OggettiDiInteresseAdapter(getApplicationContext(), oggetti_di_interesse);
                list_view_oggetti.setAdapter(customAdapter);



            } else
            {
                Log.w("error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
            }
        });
    }
}