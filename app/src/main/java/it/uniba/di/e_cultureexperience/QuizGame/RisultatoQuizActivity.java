package it.uniba.di.e_cultureexperience.QuizGame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuogoDiInteresse;
import it.uniba.di.e_cultureexperience.R;

public class RisultatoQuizActivity extends AppCompatActivity {

    private ArrayList<QuesitoQuiz> list;

    //Roba per aggiornamento del database per la classifica
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    //Roba per Output classifica aggiornata
    private ArrayList<SingolaRigaClassifica> classificaList = new ArrayList<>();
    private ListView listViewClassifica;
    private ImageView immagineOggetto;

    //pulsante riprova
    private Button riprovaBtn;
    private Button esciBtn;
    private Button shareBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risultato_quiz);

        final String idOggettoDiInteresse = getIntent().getExtras().getString("idOggetto");

        //prendo i quesiti passati dall' intent
        list = getIntent().getExtras().getParcelableArrayList("quesiti");
        int numeroRisposteCorrette = getIntent().getIntExtra("RISPOSTA_CORRETTA", 0);
        int numeroRisposteSbagliate = getIntent().getIntExtra("RISPOSTA_SBAGLIATA", 0);
        int numeroRisposteTotali = numeroRisposteSbagliate + numeroRisposteCorrette;

        //variabile punteggio quiz, la uso per condividerla se clicca il pulsante shareBTn
        AtomicLong punteggioMassimo= new AtomicLong();

        //S T A R T - Scrittura/eventuale aggiornamento classifica punteggio quiz
        String collectionPath = "oggetti/" + idOggettoDiInteresse + "/classificaQuiz";

        immagineOggetto = findViewById(R.id.immagine);
        //carico url immagine e la faccio vedere a schermo
        String urlImmagineOggetto = getIntent().getExtras().getString("url");
        Picasso.with(this)
                .load(urlImmagineOggetto)
                .into(immagineOggetto);

        //carico il nome del percorso (per condividelo se clicca il bottone share)
        String nomeOggetto = getIntent().getExtras().getString("nomeOggetto");

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    final int sizeDataBase = task.getResult().size();

                    if (task.isSuccessful()) {
                        //Caso in cui il Database è vuoto
                        if(sizeDataBase == 0){
                            ricercaNicknameConScrittura(numeroRisposteCorrette, collectionPath);
                        //Caso in cui esiste un vecchio punteggio aggiorno, altrimenti no
                        }else{

                            boolean recordTrovato = false;
                            int countRigaClassifica = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                countRigaClassifica++;

                                String idDataBase = document.getString("idUtente");
                                long punteggioDataBase = document.getLong("punteggio");
                                punteggioMassimo.set(punteggioDataBase);
                                //Controllo se c'è stato già un punteggio vecchio fatto dallo stesso utente
                                if(Objects.equals(fAuth.getUid(), idDataBase)){
                                    recordTrovato = true;

                                    //se c'è stato gia un suo puntegio registrato, allora controllo il punteggio se è da aggiornare
                                    if(numeroRisposteCorrette > punteggioDataBase){

                                        String idDocumentoDaEliminare = document.getId();

                                        //Elimino il documento vecchio dove c'è il punteggio minore
                                        db.collection(collectionPath)
                                                .document(idDocumentoDaEliminare)
                                                .delete()
                                                .addOnSuccessListener(unused -> Toast.makeText(RisultatoQuizActivity.this, "Aggiornamento classifica completato!", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Log.w("ELIMINAZIONE FALLITA", e));//fine delete
                                        //Dopo aver eliminato quello vecchio, scrivo sul database il punteggio nuovo (new record)
                                        //Ricerco il nickname dell'utente attuale e creo HashMap
                                        ricercaNicknameConScrittura(numeroRisposteCorrette, collectionPath);
                                    }
                                }
                                //Se sono arrivato alla fine e non ho ancora trovato l'utente, allora scrivo perchè vuol dire che è il suo primo record effettuato del quiz
                                if(countRigaClassifica == sizeDataBase && !recordTrovato){
                                    ricercaNicknameConScrittura(numeroRisposteCorrette, collectionPath);
                                }
                            }//fine for
                        }
                    }
                });

        //F I N I S H - Scrittura/eventuale aggiornamento classifica punteggio quiz


        //S T A R T - Mostrare Output classifica aggiornata

        listViewClassifica = findViewById(R.id.lista_classifica);
        letturaClassifica(collectionPath);

        //F I N I S H - Mostrare Output classifica aggiornata

        riprovaBtn = findViewById(R.id.tryAgainBtn);
        esciBtn = findViewById(R.id.btnEsci);
        shareBtn = findViewById(R.id.btnCondividi);
        TextView risultato = findViewById(R.id.risultatoText);

        //modifico il colore dei pulsanti di questa activity
        setColorButton();

        risultato.setText(numeroRisposteCorrette + "/" + numeroRisposteTotali);



        riprovaBtn.setOnClickListener(v -> {

            GradientDrawable bgShape1 = (GradientDrawable) riprovaBtn.getBackground();
            bgShape1.setColor(Color.parseColor("#000000"));
            riprovaBtn.setTextColor(Color.parseColor("#ffffff"));

            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("quesiti", list);
            intent.putExtra("idOggetto", idOggettoDiInteresse);
            intent.putExtra("url", urlImmagineOggetto);
            getApplicationContext().startActivity(intent);
            finish();
        });

        esciBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cambio il colore in nero (solo al tocco)
                GradientDrawable bgShape1 = (GradientDrawable) esciBtn.getBackground();
                bgShape1.setColor(Color.parseColor("#000000"));
                esciBtn.setTextColor(Color.parseColor("#ffffff"));

                //esco dall'activity
                Intent i = new Intent(getApplicationContext(), DashboardMeteActivity.class);
                startActivity(i);
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cambio il colore in nero (solo al tocco)
                GradientDrawable bgShape1 = (GradientDrawable) shareBtn.getBackground();
                bgShape1.setColor(Color.parseColor("#000000"));
                shareBtn.setTextColor(Color.parseColor("#ffffff"));

                //avvio intent share
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "nel percorso ''"+nomeOggetto+ "'' il mio miglior punteggio è:"+ punteggioMassimo+ "/"+numeroRisposteTotali+"\n\n" +"Prova a battermi!"+ "\n\n" + "Scaricati l'app ECulture-Experience!");
                startActivity(Intent.createChooser(intent, "Condividi il luogo di interesse"));

            }
        });

    }


    public void scritturaDataBase(Map<String, Object> utente, String collectionPath){
        //scrittura
        db.collection(collectionPath)
                .add(utente)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

    public void ricercaNicknameConScrittura(int numeroRisposteCorrette, String collectionPath){
        Map<String, Object> utente = new HashMap<>();
        //Ricerco il nickname dell'utente actual e creo HashMap
        db.collection("utenti")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if(task.getResult() != null){

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String idUtenteDataBase = document.getString("idUtente");

                                if(Objects.equals(fAuth.getUid(), idUtenteDataBase)){
                                    String nicknameDatabase = document.getString("nickname");
                                    utente.put("idUtente", fAuth.getUid());
                                    utente.put("nickname", nicknameDatabase);
                                    utente.put("punteggio", numeroRisposteCorrette);
                                    scritturaDataBase(utente, collectionPath);
                                    letturaClassifica(collectionPath);
                                }
                            }
                        }
                    }
                });
    }

    public void letturaClassifica(String collectionPath){


        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        int sizeDataBase = task.getResult().size(),  countRigaClassifica = 0;

                        //svuoto per non creare doppioni ogni volta che viene riprovato il quiz
                        classificaList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            countRigaClassifica++;

                            SingolaRigaClassifica temp = document.toObject(SingolaRigaClassifica.class);

                            classificaList.add(temp);

                            if(sizeDataBase == countRigaClassifica){
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

    public void setColorButton(){
        GradientDrawable bgShape1 = (GradientDrawable) riprovaBtn.getBackground();
        bgShape1.setColor(Color.parseColor("#ffffff"));
        GradientDrawable bgShape2 = (GradientDrawable) esciBtn.getBackground();
        GradientDrawable bgShape3 = (GradientDrawable) shareBtn.getBackground();
        bgShape2.setColor(Color.parseColor("#ffffff"));
        bgShape3.setColor(Color.parseColor("#ffffff"));
    }
}