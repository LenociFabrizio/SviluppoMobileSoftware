package it.uniba.di.e_cultureexperience.QuizGame;

import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuogoDiInteresse;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.MostraLuogoDiInteresseActivity;
import it.uniba.di.e_cultureexperience.R;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity {
    //OGGETTI PER PROGRESS BAR
    ProgressBar progressBar;
    CountDownTimer countDownTimer;
    int timerValue = 0;

    //OGGETTI PER LISTA CONTENENTI LE DOMANDE
    ArrayList<QuesitoQuiz> list;
    List<QuesitoQuiz> allQuestionsLilst;
    QuesitoQuiz modelClass;
    int i = 0;

    //OGGETTI PER IL LAYOUT
    TextView domanda, numeroDomanda;
    Button primaOpzione, secondaOpzione, terzaOpzione;

    int posizioneCliccata=0;

    //OGGETTI PER CONTARE RISPOSTE CORRETTE E SBAGLIATE - PER RISULTATO FINALE IN "RisultatoQuizActivity.java"
    int correttaCount = 0, sbagliataCount = 0;

    //OGGETTI PER FIREBASE
    FirebaseFirestore db;

    ImageView exitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        numeroDomanda = findViewById(R.id.numeroDomandaTV);

        //BARRA DEL TIMER
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(timerValue);
        countDownTimer = new CountDownTimer(20000,1000) {
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

                Intent intent = new Intent(DashboardActivity.this, TimeOutActivity.class);
                startActivity(intent);

            }
        }.start();

        //LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");
        //MostraLuogoDiInteresseActivity ldi=new MostraLuogoDiInteresseActivity();

        //ldi.isLuogoPreferito(luogo);

        //ImageView immagine = findViewById(R.id.immagine);
        /*
        Picasso.with(this)
                .load(ldi.getUrl_immagine())
                .into(immagine);*/

        domanda = findViewById(R.id.domanda);
        primaOpzione = findViewById(R.id.primaRisposta);
        secondaOpzione = findViewById(R.id.secondaRisposta);
        terzaOpzione = findViewById(R.id.terzaRisposta);

        coloraShapeButton();

        //prendo i quesiti passati dall' intent
        list = getIntent().getExtras().getParcelableArrayList("quesiti");
        setListenersToViews();
        assegnazioneList();
        setAllData();

    }

    private void setListenersToViews(){
        primaOpzione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posizioneCliccata=1;
                prossimaDomanda(primaOpzione);
            }
        });

        secondaOpzione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posizioneCliccata=2;
                prossimaDomanda(secondaOpzione);
            }
        });

        terzaOpzione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posizioneCliccata=3;
                prossimaDomanda(terzaOpzione);
            }
        });

    }
    public void setAllData() {
        domanda.setText(modelClass.getDomanda());
        primaOpzione.setText(modelClass.getPrimaOpzione());
        secondaOpzione.setText(modelClass.getSecondaOpzione());
        terzaOpzione.setText(modelClass.getTerzaOpzione());
    }

    public void assegnazioneList() {
        allQuestionsLilst = list;
        Collections.shuffle(allQuestionsLilst);
        modelClass = list.get(i);
    }

    private boolean esitoOpzione(Button opzione) {
        return opzione.getText().equals(modelClass.getRispostaCorretta());
    }

    public void prossimaDomanda(Button opzione) {

        //Conto quali sono le opzioni corrette o sbagliate totali
        if(esitoOpzione(opzione)) {
            correttaCount++;
            // qui vado a modificare il colore del shape button non rendendolo quadrato ma sempre ovale
            GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
            bgShape1.setColor( Color.parseColor("#00FF00"));

        }

        else {
            sbagliataCount++;
            if(posizioneCliccata==2){
                GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
                GradientDrawable bgShape2 = (GradientDrawable) secondaOpzione.getBackground();
                bgShape1.setColor( Color.parseColor("#00FF00"));
                bgShape2.setColor( Color.parseColor("#ff0000"));
            }
            if(posizioneCliccata==3){
                GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
                GradientDrawable bgShape2 = (GradientDrawable) terzaOpzione.getBackground();
                bgShape1.setColor( Color.parseColor("#00FF00"));
                bgShape2.setColor( Color.parseColor("#ff0000"));
            }

        }

        //Se non si trova nell'ultima domanda vado nella domanda successiva, altrimenti vado in RisultatoQuizActivity
        if(i < list.size() - 1) {
            i++;
            //"RUVO" UN PO DI TEMPO PER FAR VEDERE SE L'UTENTE HA FATTO ERRORI E PROSEGUO IN AUTOMATICO CON A DOMANDA SUCCESSIVA
            Timer timer= new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    modelClass = list.get(i);
                    setAllData();
                }
            },420);

        }else {
            //"RUBO" TEMPO PER FAR VEDERE ALL'UTENTE L'ERRORE E IN AUTOMATICO ENTRO NELL'ALTRA ACTIVITY
            Timer timer= new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(DashboardActivity.this, RisultatoQuizActivity.class);
                    intent.putExtra("quesiti", list);
                    intent.putExtra("RISPOSTA_CORRETTA", correttaCount);
                    intent.putExtra("RISPOSTA_SBAGLIATA", sbagliataCount);
                    startActivity(intent);
                    finish();
                }
            },420);
        }
    }

    //If user press home button and come in the game from memory then this
    //method will continue the timer from the previous time it left
    @Override
    protected void onRestart() {
        super.onRestart();
        countDownTimer.start();
    }
    //When activity is destroyed then this will cancel the timer
    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }
    //This will pause the time
    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    public void  coloraShapeButton(){
        GradientDrawable bgShape1 = (GradientDrawable) primaOpzione.getBackground();
        GradientDrawable bgShape2 = (GradientDrawable) secondaOpzione.getBackground();
        GradientDrawable bgShape3 = (GradientDrawable) terzaOpzione.getBackground();
        bgShape1.setColor( Color.parseColor("#ffffff"));
        bgShape2.setColor( Color.parseColor("#ffffff"));
        bgShape3.setColor( Color.parseColor("#ffffff"));
    }

}