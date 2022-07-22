package it.uniba.di.e_cultureexperience;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StartGame extends AppCompatActivity {

    private Button startButton;
    private TextView descrizione;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        startButton = findViewById(R.id.buttonPlay);
        descrizione = findViewById(R.id.descrizioneQuizTV);
        //todo: Bisogna passare l'"idPercorso" da un'altra intent
        Percorso percorso = getIntent().getExtras().getParcelable("percorso");
        String percorsoSelezionato = percorso.getId();

        //Al click del bottone, inizio a giocare
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartGame.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        //setDescrizione(percorsoSelezionato);
    }

   /* public void setDescrizione(String idPercorso){
        db.collection("quizGame").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult())
                {

                    String idPercorsoDatabase = document.getString("idPercorso");
                    String descrizioneDatabase = document.getString("descrizione");

                    if(idPercorso.equals(idPercorsoDatabase))
                        descrizione.setText(descrizioneDatabase);

                }

            } else {
                Log.w("error", "Error getting documents.", task.getException());
            }
        });
    }*/
}