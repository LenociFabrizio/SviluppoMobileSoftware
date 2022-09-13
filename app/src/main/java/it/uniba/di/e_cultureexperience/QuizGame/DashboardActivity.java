package it.uniba.di.e_cultureexperience.QuizGame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.QRScanner.QRScanner;
import it.uniba.di.e_cultureexperience.R;

public class DashboardActivity extends AppCompatActivity {
    //OGGETTI PER PROGRESS BAR
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private int timerValue = 0;

    //OGGETTI PER LISTA CONTENENTI LE DOMANDE
    private ArrayList<QuesitoQuiz> list;
    private List<QuesitoQuiz> allQuestionsLilst;
    private QuesitoQuiz modelClass;
    int i = 0;

    //OGGETTI PER IL LAYOUT
    private TextView domanda, numeroDomanda;
    private Button primaOpzione, secondaOpzione, terzaOpzione;

    private ImageView immagineOggetto;

    //OGGETTI PER CONTARE RISPOSTE CORRETTE E SBAGLIATE - PER RISULTATO FINALE IN "RisultatoQuizActivity.java"
    int correttaCount = 0, sbagliataCount = 0;

    //contatore per il click dell'utente
    int posizioneCliccata = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        numeroDomanda = findViewById(R.id.numeroDomandaTV);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(timerValue);

        domanda = findViewById(R.id.domanda);
        primaOpzione = findViewById(R.id.primaRisposta);
        secondaOpzione = findViewById(R.id.secondaRisposta);
        terzaOpzione = findViewById(R.id.terzaRisposta);
        immagineOggetto = findViewById(R.id.immagine);

        //carico url immagine e la faccio vedere a schermo
        String urlImmagineOggetto = getIntent().getExtras().getString("url");

        Picasso.with(this)
                .load(urlImmagineOggetto)
                .into(immagineOggetto);

        countDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of progress" + timerValue + millisUntilFinished);
                timerValue++;
                progressBar.setProgress((int) timerValue * 100 / (20000 / 1000));
            }

            @Override
            public void onFinish() {
                timerValue++;
                progressBar.setProgress(100);

                domanda.setText("@string/timeOut");

            }
        }.start();

        setColorButtons();

        //prendo i quesiti passati dall' intent
        list = getIntent().getExtras().getParcelableArrayList("quesiti");
        String idOggettoDiInteresse = getIntent().getExtras().getString("idOggetto");
        setListenersToViews(idOggettoDiInteresse);
        assegnazioneList();
        setAllData();

        //menu
        onCreateBottomNavigation();

    }

    /**
     * A seconda della scelta dell'utente il bottone cambia colore in base alla risposta: se è corretta verde, altrimenti rosso
     * @param idOgg
     */
    private void setListenersToViews(String idOgg){

        primaOpzione.setOnClickListener(v -> {
            posizioneCliccata = 1;
            prossimaDomanda(primaOpzione, idOgg);
        });

        secondaOpzione.setOnClickListener(v -> {
            posizioneCliccata = 2;
            prossimaDomanda(secondaOpzione, idOgg);
        });

        terzaOpzione.setOnClickListener(v -> {
            posizioneCliccata = 3;
            prossimaDomanda(terzaOpzione, idOgg);
        });
    }

    public void setAllData () {
        domanda.setText(modelClass.getDomanda());
        primaOpzione.setText(modelClass.getPrimaOpzione());
        secondaOpzione.setText(modelClass.getSecondaOpzione());
        terzaOpzione.setText(modelClass.getTerzaOpzione());
    }

    public void assegnazioneList () {
        allQuestionsLilst = list;
        Collections.shuffle(allQuestionsLilst);
        modelClass = list.get(i);
    }

    private boolean esitoOpzione (Button opzione){
        return opzione.getText().equals(modelClass.getRispostaCorretta());
    }

    public void prossimaDomanda (Button opzione, String idOgg){
        //Conto quali sono le opzioni corrette o sbagliate totali
        if (esitoOpzione(opzione)) {
            correttaCount++;
            // qui vado a modificare il colore del shape button non rendendolo quadrato ma sempre ovale
            GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
            bgShape1.setColor(Color.parseColor("#00FF00"));

        } else {

            sbagliataCount++;

            if (posizioneCliccata == 2) {
                GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
                GradientDrawable bgShape2 = (GradientDrawable) secondaOpzione.getBackground();
                bgShape1.setColor(Color.parseColor("#00FF00"));
                bgShape2.setColor(Color.parseColor("#ff0000"));
            }
            if (posizioneCliccata == 3) {
                GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
                GradientDrawable bgShape2 = (GradientDrawable) terzaOpzione.getBackground();
                bgShape1.setColor(Color.parseColor("#00FF00"));
                bgShape2.setColor(Color.parseColor("#ff0000"));
            }

        }

        //Se non si trova nell'ultima domanda vado nella domanda successiva, altrimenti vado in RisultatoQuizActivity
        if (i < list.size() - 1) {
            i++;
            modelClass = list.get(i);
            setAllData();
        } else {
            Intent intent = new Intent(DashboardActivity.this, RisultatoQuizActivity.class);
            //Prendo l'oggetto passato dall'intent
            String urlImmagineOggetto = getIntent().getExtras().getString("url");

            intent.putExtra("idOggetto", idOgg);
            intent.putExtra("quesiti", list);
            intent.putExtra("RISPOSTA_CORRETTA", correttaCount);
            intent.putExtra("RISPOSTA_SBAGLIATA", sbagliataCount);
            intent.putExtra("url",urlImmagineOggetto);
            startActivity(intent);
            finish();
        }
    }

    //If user press home button and come in the game from memory then this
    //method will continue the timer from the previous time it left
    @Override
    protected void onRestart () {

        super.onRestart();
        countDownTimer.start();
    }

    //When activity is destroyed then this will cancel the timer
    @Override
    protected void onStop () {
        super.onStop();
        countDownTimer.cancel();
    }

    //This will pause the time
    @Override
    protected void onPause () {
        super.onPause();
        countDownTimer.cancel();
    }

    public void setColorButtons(){
        GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
        GradientDrawable bgShape2 = (GradientDrawable) secondaOpzione.getBackground();
        GradientDrawable bgShape3 = (GradientDrawable) terzaOpzione.getBackground();
        bgShape1.setColor(Color.parseColor("#ffffff"));
        bgShape2.setColor(Color.parseColor("#ffffff"));
        bgShape3.setColor(Color.parseColor("#ffffff"));
    }

    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.share);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), DashboardMeteActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_scan:
                    startActivity(new Intent(getApplicationContext(), QRScanner.class));
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
}