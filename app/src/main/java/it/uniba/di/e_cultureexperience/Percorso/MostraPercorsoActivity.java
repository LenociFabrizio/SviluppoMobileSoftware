package it.uniba.di.e_cultureexperience.Percorso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import it.uniba.di.e_cultureexperience.LuogoDiInteresse.MostraLuogoDiInteresseActivity;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettiDiInteresseAdapter;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.R;

public class MostraPercorsoActivity extends AppCompatActivity {
    private TextView nomePercorso, descrizionePercorso, durataPercorso;
    private ListView listViewOggetti;
    private ArrayList<OggettoDiInteresse> oggettoDiInteresse = new ArrayList<>();
    private ExtendedFloatingActionButton avviaPercorsoButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_percorso);

        listViewOggetti = findViewById(R.id.lista_oggetti);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //prendo l' oggetto passato dall' intent
        Percorso percorso = getIntent().getExtras().getParcelable("percorso");

        getSupportActionBar().setTitle(percorso.getNome());

        //prendo i riferimenti degli elementi del layout
        nomePercorso = findViewById(R.id.nome);
        descrizionePercorso = findViewById(R.id.descrizione);
        durataPercorso = findViewById(R.id.durata);
        avviaPercorsoButton = findViewById(R.id.avviaPercorsoBtn);
        nomePercorso.setText(percorso.getNome());
        descrizionePercorso.setText(percorso.getDescrizione());
        durataPercorso.setText(getString(R.string.durata)+ Integer.toString(percorso.getDurata())+getString(R.string.minutes));

        avviaPercorsoButton.setOnClickListener(v -> {
            //Devo passare gli oggetti che sono contenuti nel percorso scelto dall'utente
            Intent intent = new Intent(this, AvviaPercorsoActivity.class);
            intent.putExtra("percorso", percorso);
            startActivity(intent);
        });

        letturaOggetti(percorso);


    }

    public void letturaOggetti(Percorso percorso){
        ArrayList<String> idOggettiList = new ArrayList<>();

        db.collection("/percorsi/"+percorso.getId()+"/oggetti")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idOggetto = document.getId();
                            idOggettiList.add(idOggetto);
                        }
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });

        //ora prendo gli oggetti che si trovano tra gli id presi prima
        db.collection("oggetti")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idOggetto = document.getId();
                            if(idOggettiList.contains(idOggetto))
                            {
                                OggettoDiInteresse temp = document.toObject(OggettoDiInteresse.class);
                                temp.setId(document.getId());
                                oggettoDiInteresse.add(temp);
                            }
                        }
                        OggettiDiInteresseAdapter customAdapter = new OggettiDiInteresseAdapter(getApplicationContext(), oggettoDiInteresse);
                        listViewOggetti.setAdapter(customAdapter);
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });
    }
}