package it.uniba.di.e_cultureexperience;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RisultatoQuizActivity extends AppCompatActivity {

    TextView risultato;

    TextView descrizioneEsito; Button esitoBtn;

    ImageView exitImageView;

    //Roba per aggiornamento del database per la classifica
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth;
    String idDocumentoDaEliminare = "";

    //Roba per Output classifica aggiornata
    ArrayList<SingolaRigaClassifica> classificaList = new ArrayList<>();
    ListView listViewClassifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risultato_quiz);

        int numeroRisposteCorrette = getIntent().getIntExtra("RISPOSTA_CORRETTA", 0);
        int numeroRisposteSbagliate = getIntent().getIntExtra("RISPOSTA_SBAGLIATA", 0);
        int numeroRisposteTotali = numeroRisposteSbagliate + numeroRisposteCorrette;

        //S T A R T - Scrittura/eventuale aggiornamento classifica punteggio quiz
        fAuth = FirebaseAuth.getInstance();

        db.collection("classificaQuiz")
                .get()
                .addOnCompleteListener(task -> {

                    final int sizeDataBase = task.getResult().size();

                    if (task.isSuccessful()) {
                        //Caso in cui il Database è vuoto
                        if(sizeDataBase == 0){

                            ricercaNicknameConScrittura(numeroRisposteCorrette);
                        //Caso in cui esiste un vecchio punteggio e, nel caso, aggiorno, altrimenti non aggiorno
                        }else{

                            boolean recordTrovato = false;
                            int count = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                count++;

                                String idDataBase = document.getString("idUtente");
                                long punteggioDataBase = document.getLong("punteggio");

                                //Controllo se c'è stato già un punteggio vecchio fatto dallo stesso utente
                                if(Objects.equals(fAuth.getUid(), idDataBase)){
                                    recordTrovato = true;
                                    //se c'è stato gia un suo puntegio registrato, allora controllo il punteggio se è da aggiornare
                                    if(numeroRisposteCorrette > punteggioDataBase){

                                        idDocumentoDaEliminare = document.getId();

                                        //Elimino il documento vecchio dove c'è il punteggio minore
                                        db.collection("classificaQuiz")
                                                .document(idDocumentoDaEliminare)
                                                .delete()
                                                .addOnSuccessListener(unused -> Toast.makeText(RisultatoQuizActivity.this, "Aggiornamento classifica completato!", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Log.w("ELIMINAZIONE FALLITA", e));//fine delete
                                        //Dopo aver eliminato quello vecchio, scrivo sul database il punteggio nuovo (new record)
                                        //Ricerco il nickname dell'utente attuale e creo HashMap
                                        ricercaNicknameConScrittura(numeroRisposteCorrette);
                                    }//Fine if confrontro punteggio
                                }
                                //Se sono arrivato alla fine e non ho ancora trovato l'utente, allora scrivo perchè vuol dire che è il suo primo record effettuato del quiz
                                if(count == sizeDataBase && recordTrovato == false){
                                    ricercaNicknameConScrittura(numeroRisposteCorrette);
                                }
                            }//fine for
                        }
                    }
                });

        //F I N I S H - Scrittura/eventuale aggiornamento classifica punteggio quiz


        //S T A R T - Mostrare Output classifica aggiornata

        listViewClassifica = findViewById(R.id.lista_classifica);
        letturaClassifica();

        //F I N I S H - Mostrare Output classifica aggiornata


        descrizioneEsito = findViewById(R.id.esitoText);
        esitoBtn = findViewById(R.id.resultButton);
        risultato = findViewById(R.id.risultatoText);

        risultato.setText(numeroRisposteCorrette + "/" + numeroRisposteTotali);

        if(numeroRisposteCorrette == numeroRisposteTotali){
            descrizioneEsito.setText("Complimenti, hai risposto a tutte le domande del quiz correttamente! Se ti sei divertito, sei libero di rifarlo");
            esitoBtn.setText("Rifacciamo!");
        }else{
            descrizioneEsito.setText("Se vuoi migliorare il tuo punteggio clicca sul bottone qui sotto!");
            esitoBtn.setText("Miglioriamo!");
        }

        esitoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RisultatoQuizActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        exitImageView = findViewById(R.id.exitResultBtn);
        exitImageView.setOnClickListener(v -> {
            Intent intent = new Intent(RisultatoQuizActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

    }


    public void scritturaDataBase(Map<String, Object> utente){
        //scrittura
        db.collection("classificaQuiz")
                .add(utente)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

    public void ricercaNicknameConScrittura(int numeroRisposteCorrette){
        Map<String, Object> utente = new HashMap<>();
        //Ricerco il nickname dell'utente actual e creo HashMap
        db.collection("utenti")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if(task.getResult() != null){

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String idUtenteDataBase = document.getString("idUtente");

                                assert idUtenteDataBase != null;
                                if(idUtenteDataBase.equals(fAuth.getUid())){
                                    String nicknameDatabase = document.getString("nickname");
                                    utente.put("idUtente", fAuth.getUid());
                                    utente.put("nickname", nicknameDatabase);
                                    utente.put("punteggio", numeroRisposteCorrette);
                                    scritturaDataBase(utente);
                                    letturaClassifica();
                                }
                            }
                        }
                    }
                });
    }

    public void letturaClassifica(){

        db.collection("classificaQuiz")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        int count = 0;
                        //svuoto per non creare doppioni ogni volta che viene riprovato il quiz
                        classificaList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            count++;

                            SingolaRigaClassifica temp = document.toObject(SingolaRigaClassifica.class);

                            classificaList.add(temp);

                            final int sizeDataBase = task.getResult().size();

                            if(sizeDataBase == count){
                                //Ordino la lista in ordine decrescente
                                classificaList.sort(Comparator.comparing(SingolaRigaClassifica::getPunteggio));
                                Collections.reverse(classificaList);

                            }
                        }//Fine for
                        PunteggioAdapter customAdapter = new PunteggioAdapter(getApplicationContext(), classificaList);
                        listViewClassifica.setAdapter(customAdapter);
                    }//Fine if
                });
    }



}