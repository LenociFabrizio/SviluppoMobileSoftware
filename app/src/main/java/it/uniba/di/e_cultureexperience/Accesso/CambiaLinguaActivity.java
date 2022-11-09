package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.uniba.di.e_cultureexperience.R;

public class CambiaLinguaActivity extends AppCompatActivity {


    private RadioGroup radioGroup;
    private RadioButton radioIta, radioEng, selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_lingua);

        Locale current = getResources().getConfiguration().locale;

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        radioGroup = findViewById(R.id.radio_group);
        radioIta = findViewById(R.id.radio_ita);
        radioEng = findViewById(R.id.radio_eng);

        if(current.getLanguage().equals("it")){
            radioIta.setChecked(true);
        }
        else{
            radioEng.setChecked(true);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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


    public void checkRadioGroup(View v){
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        selectedLanguage =findViewById(radioButtonId);
        String textId = selectedLanguage.toString();
        if(textId.substring(textId.lastIndexOf("/radio")).contains("/radio_ita")){
            setLocale("it");
        }
        else{
            setLocale("en-rGB");
        }

    }
}