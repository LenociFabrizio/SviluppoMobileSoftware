package it.uniba.di.e_cultureexperience.LuogoDiInteresse;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuogoDiInteresse;
import it.uniba.di.e_cultureexperience.Percorso.PercorsiAdapter;
import it.uniba.di.e_cultureexperience.Percorso.Percorso;
import it.uniba.di.e_cultureexperience.R;
import it.uniba.di.e_cultureexperience.databinding.ActivityMostraLuogoDiInteresse2Binding;

public class MostraLuogoDiInteresse2 extends AppCompatActivity {

    ArrayList<Percorso> percorsi;
    ListView list_view_percorsi;
    private ActivityMostraLuogoDiInteresse2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        percorsi = new ArrayList<>();
        list_view_percorsi = findViewById(R.id.lista_percorsi);

        //prendo l' oggetto passato dall' intent
        LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");
        //prendo i riferimenti degli elementi del layout
        ImageView immagine = (ImageView) findViewById(R.id.immagine);
        TextView descrizione_luogo = (TextView) findViewById(R.id.descrizione);

        //riempio gli elementi del layout
        Picasso.with(this)
                .load(luogo.getUrl_immagine())
                .into(immagine);

        descrizione_luogo.setText(luogo.getDescrizione());

        //OGGETTI PER FIREBASE
        FirebaseFirestore db;
        DocumentReference docRef;


        db = FirebaseFirestore.getInstance();
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

        binding = ActivityMostraLuogoDiInteresse2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

    }
}