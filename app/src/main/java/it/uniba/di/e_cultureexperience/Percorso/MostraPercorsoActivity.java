package it.uniba.di.e_cultureexperience.Percorso;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuogoDiInteresse;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettiDiInteresseAdapter;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.QRScanner.QRScanner;
import it.uniba.di.e_cultureexperience.R;

public class MostraPercorsoActivity extends AppCompatActivity {
    private TextView nomePercorso, descrizionePercorso, durataPercorso, numeroVotazioni;
    private ListView listViewOggetti;
    private RatingBar ratingStarsMedio;
    private Button openDialogBtn;

    private ArrayList<OggettoDiInteresse> oggettiDiInteresse = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private String collectionPathValutazione, collectionPathOggetti;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_percorso);

        Toast.makeText(getApplicationContext(),"Percorso scelto",Toast.LENGTH_SHORT).show();


        listViewOggetti = findViewById(R.id.lista_oggetti);

<<<<<<< Updated upstream
        Toolbar mToolbar = findViewById(R.id.toolbar_luogodiinteresse);

        //prendo l' oggetto passato dall' intent
        Percorso percorso = getIntent().getExtras().getParcelable("percorso");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(percorso.getNome());
=======
        /*
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        */
        //prendo l' oggetto passato dall' intent
        Percorso percorso = getIntent().getExtras().getParcelable("percorso");

        //getSupportActionBar().setTitle(percorso.getNome());
>>>>>>> Stashed changes


        //prendo i riferimenti degli elementi del layout
        nomePercorso = findViewById(R.id.nome);
        descrizionePercorso = findViewById(R.id.descrizione);
        durataPercorso = findViewById(R.id.durata);
        nomePercorso.setText(percorso.getNome());
        descrizionePercorso.setText(percorso.getDescrizione());
        durataPercorso.setText(getString(R.string.durata)+ Integer.toString(percorso.getDurata())+getString(R.string.minutes));
        ratingStarsMedio = findViewById(R.id.ratingBar);
        openDialogBtn = findViewById(R.id.btnOpenDialog);
        numeroVotazioni = findViewById(R.id.numeroVotazioniTxt);

        //S T A R T - Rating stars
        //TODO: decidere posizione del Rating stars
        collectionPathValutazione = "percorsi/" + percorso.getId() + "/valutazione";

        calcoloMediaValutazione(collectionPathValutazione);

        openDialogBtn.setOnClickListener(v -> {
            writeRating(percorso, collectionPathValutazione);
        });
        //F I N I S H - Rating stars

        collectionPathOggetti = "percorsi/" + percorso.getId() + "/oggetti";
        letturaOggetti(collectionPathOggetti);

        onCreateBottomNavigation();

    }

    public void letturaOggetti(String collectionPath){
        ArrayList<String> idOggettiList = new ArrayList<>();

        db.collection(collectionPath)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idOggetto = document.getId();
                            idOggettiList.add(idOggetto);
                        }
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });

        //ora prendo gli oggetti che si trovano tra gli id presi prima
        db.collection("oggetti")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idOggetto = document.getId();
                            if(idOggettiList.contains(idOggetto))
                            {
                                OggettoDiInteresse temp = document.toObject(OggettoDiInteresse.class);
                                temp.setId(document.getId());
                                oggettiDiInteresse.add(temp);
                            }
                        }
                        OggettiDiInteresseAdapter customAdapter = new OggettiDiInteresseAdapter(getApplicationContext(), oggettiDiInteresse);
                        listViewOggetti.setAdapter(customAdapter);
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });
    }


    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_home);

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.secondary_top_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:

                Percorso percorso = getIntent().getExtras().getParcelable("percorso");

                Intent intent =new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Vieni a vedere il"+percorso.getNome()+"\n\n"+percorso.getDescrizione()+"\n\n"+"Scaricati l'app ECulture-Experience!");

                startActivity(Intent.createChooser(intent,"Condividi il luogo di interesse"));
                return true;

            case R.id.favourite_btn:
                //va aggiunta la stessa funzione
                //onFavoriteToggleClick();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }


    }

    /**
     * Funzione atomica di scrittura su db dato un map<string, object> e la collectionPath
     * @param valutazione
     * @param collectionPath
     */
    public void addDoc(Map<String, Object> valutazione, String collectionPath){

        db.collection(collectionPath)
                .add(valutazione)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

    /**
     * Funzione atomica di eliminazione di un documento su db attraverso la collectinoPath e l'id del documento da eliminare
     * @param collectionPath
     * @param documentReference
     */
    public void deleteDoc(String collectionPath, String documentReference)
    {
        db.collection(collectionPath).document(documentReference)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("Delete", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e ->
                        Log.w("Delete", "Error deleting document", e));
    }

    /**
     * scrive la valutazione su db: se il db è vuoto scrivo la nuova valutazione,
     * se esiste già allora l'aggiorno
     * altrimenti se non è vuota ma la valutazione dal seguente utente non è stata ancora data, allora l'aggiungo
     * @param collectionPath
     * @param rating
     */
    public void scritturaValutazioneDatabase(String collectionPath, float rating){
        Map<String, Object> valutazione = new HashMap<>();
        valutazione.put("idUtente", fAuth.getUid());
        valutazione.put("valutazione", rating);

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        int sizeDataBase = task.getResult().size(), singolaRigaDatabase = 0;
                        boolean valutazioneEsistente = false;
                        if(sizeDataBase != 0){

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                singolaRigaDatabase++;

                                String idUtenteDataBase = document.getString("idUtente");
                                //se è stata trovata una valutazione fatta dallo stesso utente la elimino e l'aggiorno
                                if(Objects.equals(fAuth.getUid(), idUtenteDataBase)){
                                    deleteDoc(collectionPath, document.getId());

                                    addDoc(valutazione, collectionPath);
                                    valutazioneEsistente = true;
                                }//fine if

                                if (!valutazioneEsistente && singolaRigaDatabase == sizeDataBase){
                                    addDoc(valutazione, collectionPath);
                                }
                            }//fine for
                        }else{
                            addDoc(valutazione, collectionPath);
                        }
                    }
                });
    }

    /**
     * Controllo se ha già inserito precedentemente una valutazione
     */
    public void letturaValutazione(String collectionPath, RatingBar rating){

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        final int sizeDataBase = task.getResult().size();
                        if (sizeDataBase != 0) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String idUtenteDatabase = document.getString("idUtente");
                                double valutazione = document.getDouble("valutazione");

                                if(idUtenteDatabase.equals(fAuth.getUid())){
                                    rating.setRating((float) valutazione);
                                }
                            }//fine for
                        }
                    }
                });
    }

    /**
     * Calcolo della media valutazione per un determinato oggetto di interesse (WIP)
     * @param collectionPath
     */
    public void calcoloMediaValutazione(String collectionPath){

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        int numeroVotanti = task.getResult().size();
                        if (numeroVotanti != 0) {
                            double sommaValutazioni = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                double valutazione = document.getDouble("valutazione");

                                sommaValutazioni = valutazione + sommaValutazioni;
                            }
                            float mediaValutazione = (float) sommaValutazioni/numeroVotanti;
                            ratingStarsMedio.setRating(mediaValutazione);
                            numeroVotazioni.setText("(" + numeroVotanti + ")");

                            ratingStarsMedio.setClickable(false);
                        }
                    }
                });


    }

    /**
     * Apre un nuovo Dialog che permette all'utente di inserire una valutazione attraverso il ratingBar a stella
     * @param percorso
     * @param collectionPathValutazione
     */
    public void writeRating(Percorso percorso, String collectionPathValutazione){

        Dialog rankDialog = new Dialog(MostraPercorsoActivity.this, R.style.Base_Theme_AppCompat_Dialog_Alert);
        rankDialog.setContentView(R.layout.rank_dialog);
        rankDialog.setCancelable(true);

        RatingBar ratingStarsDialog = (RatingBar) rankDialog.findViewById(R.id.ratingBarDialog);
        //Setting iniziale del ratingStars con il voto precedentemente inserito
        letturaValutazione(collectionPathValutazione, ratingStarsDialog);
        //Scrittura e aggiornamento del voto
        ratingStarsDialog.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            //Se la valutazione data è maggiore di 0 (ha votato), allora salvo la valutazione in un contenitore unico per tutti gli utenti
            if(rating > 0){
                scritturaValutazioneDatabase(collectionPathValutazione, rating);
            }
        });

        TextView titoloDialog = (TextView) rankDialog.findViewById(R.id.titleRating);
        titoloDialog.setText("Dai un voto a: " + percorso.getNome());

        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(v -> rankDialog.dismiss());

        rankDialog.show();

    }


}