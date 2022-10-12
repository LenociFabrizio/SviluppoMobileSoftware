package it.uniba.di.e_cultureexperience.QuizGame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.R;

//todo: cambiare nome activity
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

        //numeroDomanda = findViewById(R.id.numeroDomandaTV);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(timerValue);

        domanda = findViewById(R.id.domanda);
        primaOpzione = findViewById(R.id.primaRisposta);
        secondaOpzione = findViewById(R.id.secondaRisposta);
        terzaOpzione = findViewById(R.id.terzaRisposta);
        immagineOggetto = findViewById(R.id.immagine);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

            /**
             * Quando il timer scade, elimino due pulsanti e ne rimane solo uno (secondaOpzione), che se cliccato esce dall'activity
             *
             */

            @Override
            public void onFinish() {
                timerValue++;
                progressBar.setProgress(100);

                domanda.setText(R.string.timeOut);
                primaOpzione.setVisibility(View.GONE);
                terzaOpzione.setVisibility(View.GONE);
                secondaOpzione.setText(R.string.vaiVia);


                secondaOpzione.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GradientDrawable bgShape1 = (GradientDrawable) secondaOpzione.getBackground();
                        bgShape1.setColor(Color.parseColor("#000000"));
                        secondaOpzione.setTextColor(Color.parseColor("#ffffff"));
                        Intent i= new Intent(DashboardActivity.this, DashboardMeteActivity.class);

                        //i.putExtra("url",urlImmagineOggetto);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }.start();

        setColorButtons();

        //prendo i quesiti passati dall' intent
        list = getIntent().getExtras().getParcelableArrayList("quesiti");
        String idOggettoDiInteresse = getIntent().getExtras().getString("idOggetto");
        setListenersToViews(idOggettoDiInteresse);
        assegnazioneList();
        setAllData();
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

            if(posizioneCliccata==1){
                // qui vado a modificare il colore del shape button non rendendolo quadrato ma sempre ovale
                GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
                bgShape1.setColor(Color.parseColor(	"#00ff00"));
            }
            if(posizioneCliccata==2){
                // qui vado a modificare il colore del shape button non rendendolo quadrato ma sempre ovale
                GradientDrawable bgShape1 = (GradientDrawable) secondaOpzione.getBackground();
                bgShape1.setColor(Color.parseColor("#00ff00"));
            }
            if(posizioneCliccata==3){
                // qui vado a modificare il colore del shape button non rendendolo quadrato ma sempre ovale
                GradientDrawable bgShape1 = (GradientDrawable) terzaOpzione.getBackground();
                bgShape1.setColor(Color.parseColor("#00ff00"));
            }

        } else {

            sbagliataCount++;

            if (posizioneCliccata == 1) {
                GradientDrawable bgShape2 = (GradientDrawable) primaOpzione.getBackground();
                bgShape2.setColor(Color.parseColor("#ff0000"));
            }
            if (posizioneCliccata == 2) {
                GradientDrawable bgShape2 = (GradientDrawable) secondaOpzione.getBackground();
                bgShape2.setColor(Color.parseColor("#ff0000"));
            }
            if (posizioneCliccata == 3) {
                GradientDrawable bgShape2 = (GradientDrawable) terzaOpzione.getBackground();
                bgShape2.setColor(Color.parseColor("#ff0000"));
            }

        }

        //Se non si trova nell'ultima domanda vado nella domanda successiva, altrimenti vado in RisultatoQuizActivity
        if (i < list.size() - 1) {
            i++;
            modelClass = list.get(i);
            setAllData();
        } else {
            Intent i = new Intent(DashboardActivity.this, RisultatoQuizActivity.class);
            //Prendo l'oggetto passato dall'intent
            String urlImmagineOggetto = getIntent().getExtras().getString("url");
            //carico il nome del percorso (per condividelo nel risultatoQuiz)
            String nomeOggetto = getIntent().getExtras().getString("nomeOggetto");


            i.putExtra("idOggetto", idOgg);
            i.putExtra("quesiti", list);
            i.putExtra("RISPOSTA_CORRETTA", correttaCount);
            i.putExtra("RISPOSTA_SBAGLIATA", sbagliataCount);
            i.putExtra("url",urlImmagineOggetto);
            i.putExtra("nomeOggetto",nomeOggetto);

            startActivity(i);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}