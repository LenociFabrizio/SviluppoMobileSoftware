package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.R;
import it.uniba.di.e_cultureexperience.QuizGame.QuesitoQuiz;
import it.uniba.di.e_cultureexperience.QuizGame.DashboardActivity;

public class MostraOggettoDiInteresse extends Activity {

    private TextView nomeOggetto, descrizioneOggetto, bluetoothOggetto;
    private ImageView immagineOggetto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oggetto_di_interesse);

        nomeOggetto = findViewById(R.id.nomeTxt);
        descrizioneOggetto = findViewById(R.id.descrizioneTxt);
        immagineOggetto = findViewById(R.id.immagineOggetto);
        bluetoothOggetto  = findViewById(R.id.bluetoothIdTxt);

        //Prendo l'oggetto passato dall'intent
        OggettoDiInteresse oggettoDiInteresse = getIntent().getExtras().getParcelable("oggettoDiInteresse");
        Log.d("OggettoDiInteresse => ", oggettoDiInteresse.toString());

        //S T A R T - set content into layout
        Picasso.with(this)
                .load(oggettoDiInteresse.getUrl_immagine())
                .into(immagineOggetto);

        nomeOggetto.setText(oggettoDiInteresse.getNome());

        descrizioneOggetto.setText(oggettoDiInteresse.getDescrizione());

        bluetoothOggetto.setText(oggettoDiInteresse.getBluetooth_id());

        //OGGETTI PER FIREBASE
        FirebaseFirestore db;
        DocumentReference docRef;

        db = FirebaseFirestore.getInstance();

        //controllo se l' oggetto ha un quiz
        db.collection("/oggetti/"+oggettoDiInteresse.getId()+"/quesiti_quiz")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //ha un quiz, rendo visibile il bottone del quiz
                    Button button = (Button) findViewById(R.id.btn_quiz);
                    button.setVisibility(View.VISIBLE);
                    //quando clicca sul bottone gli passo l' array contenente i quesiti
                    ArrayList<QuesitoQuiz> quesiti = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        QuesitoQuiz temp = document.toObject(QuesitoQuiz.class);
                        quesiti.add(temp);
                    }
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //quando viene premuto, lancia l' intent esplicito
                            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                            i.putExtra("quesiti", quesiti);
                            getApplicationContext().startActivity(i);
                        }
                    });
                } else {
                    //non ha nessun quiz, rimane invisibile
                    Log.w("ENDRIT", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                }
            }
        });
        //F I N I S H

    }
}