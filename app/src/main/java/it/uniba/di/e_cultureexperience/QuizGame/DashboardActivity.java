package it.uniba.di.e_cultureexperience.QuizGame;

import static android.content.ContentValues.TAG;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    //OGGETTI PER CONTARE RISPOSTE CORRETTE E SBAGLIATE - PER RISULTATO FINALE IN "RisultatoQuizActivity.java"
    int correttaCount = 0, sbagliataCount = 0;

    private ImageView exitImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        numeroDomanda = findViewById(R.id.numeroDomandaTV);

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

        domanda = findViewById(R.id.domanda);
        primaOpzione = findViewById(R.id.primaRisposta);
        secondaOpzione = findViewById(R.id.secondaRisposta);
        terzaOpzione = findViewById(R.id.terzaRisposta);

        //prendo i quesiti passati dall' intent
        list = getIntent().getExtras().getParcelableArrayList("quesiti");
        String idOggettoDiInteresse = getIntent().getExtras().getString("idOggetto");
        setListenersToViews(idOggettoDiInteresse);
        assegnazioneList();
        setAllData();

    }

    private void setListenersToViews(String idOgg){
        primaOpzione.setOnClickListener(v -> prossimaDomanda(primaOpzione, idOgg));

        secondaOpzione.setOnClickListener(v -> prossimaDomanda(secondaOpzione, idOgg));

        terzaOpzione.setOnClickListener(v -> prossimaDomanda(terzaOpzione, idOgg));

        exitImageView = findViewById(R.id.exitImg);
        exitImageView.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
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

    public void prossimaDomanda(Button opzione, String idOgg) {

        //Conto quali sono le opzioni corrette o sbagliate totali
        if(esitoOpzione(opzione))
            correttaCount++;
        else
            sbagliataCount++;

        //Se non si trova nell'ultima domanda vado nella domanda successiva, altrimenti vado in RisultatoQuizActivity
        if(i < list.size() - 1) {
            i++;
            modelClass = list.get(i);
            setAllData();
        }else {
            Intent intent = new Intent(DashboardActivity.this, RisultatoQuizActivity.class);
            intent.putExtra("idOggetto", idOgg);
            intent.putExtra("quesiti", list);
            intent.putExtra("RISPOSTA_CORRETTA", correttaCount);
            intent.putExtra("RISPOSTA_SBAGLIATA", sbagliataCount);
            startActivity(intent);
            finish();
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
}