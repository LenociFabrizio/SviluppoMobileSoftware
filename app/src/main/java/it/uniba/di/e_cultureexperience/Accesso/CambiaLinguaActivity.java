package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.uniba.di.e_cultureexperience.R;

public class CambiaLinguaActivity extends AppCompatActivity implements ListItemAdapter.ItemClickListener{

    private RecyclerView listaMenu;
    private List<String> menuArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_lingua);

        Locale current = getResources().getConfiguration().locale;
        Log.e("lingua", current.getLanguage());


        listaMenu = findViewById(R.id.lista_menu);
        listaMenu.setLayoutManager(new LinearLayoutManager(this));

        ListItemAdapter adapter = new ListItemAdapter(menuArray,this);
        adapter.setClickListener(this);
        listaMenu.setAdapter(adapter);

        menuArray.add("Italiano");
        menuArray.add("Inglese");


    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, ProfileActivity.class);
        finish();
        startActivity(refresh);
    }

    @Override
    public void onItemClick(View view, int position) {
        if(position == 0)
            setLocale("it");
        if(position == 1)
            setLocale("en-rGB");
    }
}