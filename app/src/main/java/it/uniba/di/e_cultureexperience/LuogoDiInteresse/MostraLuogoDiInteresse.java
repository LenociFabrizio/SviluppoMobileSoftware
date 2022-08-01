package it.uniba.di.e_cultureexperience.LuogoDiInteresse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.Accesso.FirstAccessActivity;
import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.Percorso.PercorsiAdapter;
import it.uniba.di.e_cultureexperience.Percorso.Percorso;
import it.uniba.di.e_cultureexperience.R;

public class MostraLuogoDiInteresse extends AppCompatActivity {
    private ArrayList<Percorso> percorsi;
    ListView list_view_percorsi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.luogo_di_interesse);

        percorsi = new ArrayList<>();
        list_view_percorsi = findViewById(R.id.lista_percorsi);

        //prendo l' oggetto passato dall' intent
        LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");

        ImageView immagine = findViewById(R.id.immagine);
        TextView descrizioneLuogo = findViewById(R.id.descrizione);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.secondary_top_menu, menu);
        return true;
    }


    public void setPercorsi(LuogoDiInteresse luogo){

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
    }
}