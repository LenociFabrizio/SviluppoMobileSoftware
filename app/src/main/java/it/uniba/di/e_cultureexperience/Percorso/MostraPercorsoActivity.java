package it.uniba.di.e_cultureexperience.Percorso;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettiDiInteresseAdapter2;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.QrCodeScanner;
import it.uniba.di.e_cultureexperience.R;

public class MostraPercorsoActivity extends AppCompatActivity {
    private TextView numeroVotazioni, mediaValutazione;
    private Button openDialogBtn;
    private RecyclerView listViewOggetti;

    private ArrayList<OggettoDiInteresse> oggettiDiInteresse = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    //todo: se il percorso è stato imporatto, nonf ar vedere il bottone valuta il percorso
    private String collectionPathValutazione;
    private Float mediaStelle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_percorso);

        listViewOggetti = findViewById(R.id.lista_oggetti);
        LinearLayoutManager OggettiLinearLayout = new LinearLayoutManager(this){

        };
        listViewOggetti.setLayoutManager(OggettiLinearLayout);
        listViewOggetti.setNestedScrollingEnabled(false);

        //setto il colore dei pulsanti
        //setColorButtons();

        //prendo l' oggetto passato dall' intent
        Percorso percorso = getIntent().getExtras().getParcelable("percorso");

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //prendo i riferimenti degli elementi del layout
        TextView nomePercorso = findViewById(R.id.nome);
        TextView descrizionePercorso = findViewById(R.id.descrizione);
        //TextView durataPercorso = findViewById(R.id.durata);
        nomePercorso.setText(percorso.getNome());
        descrizionePercorso.setText(percorso.getDescrizione());
        openDialogBtn = findViewById(R.id.btnVotoDelPercorso);
        numeroVotazioni = findViewById(R.id.numeroVotazioniTxt);
        mediaValutazione = findViewById(R.id.rating_avg);

        //S T A R T - Rating stars
        collectionPathValutazione = "percorsi/" + percorso.getId() + "/valutazione";

        calcoloMediaValutazione(collectionPathValutazione);

        String collectionPathOggetti = "percorsi/" + percorso.getId() + "/oggetti";

        controlloPercorsoImportato(percorso, collectionPathOggetti);
        openDialogBtn.setOnClickListener(v -> writeRating(collectionPathValutazione));
        //F I N I S H - Rating stars

        onCreateBottomNavigation();

    }

    /**
     * Se il percorso è stato importato, nascondo il bottone che permette la valutazione di quest'ultimo
     */
    public void controlloPercorsoImportato(Percorso percorso, String collectionPathOggetti){
        AtomicBoolean isPercorsoImportato = new AtomicBoolean(true);
        db.collection("percorsi")
                .get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()){

                       int count = 0;
                       final int sizeDataBase = task.getResult().size();
                       for (QueryDocumentSnapshot document : task.getResult()){
                           count++;
                           String nomePercorsoDatabase = document.getString("nome");

                           //Se il percorso esiste nel database, allora, non è stato importato
                           if (nomePercorsoDatabase.equals(percorso.getNome())) {
                               isPercorsoImportato.set(false);
                           }

                           if (count == sizeDataBase && isPercorsoImportato.get()){
                               Intent intent = getIntent();
                               Bundle args = intent.getBundleExtra("BUNDLE");
                               ArrayList<OggettoDiInteresse> oggettiDiInteresseImportati = (ArrayList<OggettoDiInteresse>) args.getSerializable("ARRAYLIST");

                               TextView text5 = findViewById(R.id.rating_max);
                               text5.setVisibility(View.INVISIBLE);
                               openDialogBtn.setVisibility(View.INVISIBLE);
                               
                               OggettiDiInteresseAdapter2 customAdapter = new OggettiDiInteresseAdapter2(getApplicationContext(), oggettiDiInteresseImportati);
                               listViewOggetti.setLayoutManager(new LinearLayoutManager(MostraPercorsoActivity.this, LinearLayoutManager.VERTICAL,false));
                               listViewOggetti.setAdapter(customAdapter);
                           }else if(count == sizeDataBase && !(isPercorsoImportato).get()){
                               letturaOggetti(collectionPathOggetti);
                           }

                       }
                   }else{
                       Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                   }
                });
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
                        OggettiDiInteresseAdapter2 customAdapter = new OggettiDiInteresseAdapter2( getApplicationContext(), oggettiDiInteresse);
                        listViewOggetti.setLayoutManager(new LinearLayoutManager(MostraPercorsoActivity.this, LinearLayoutManager.VERTICAL,false));
                        listViewOggetti.setAdapter(customAdapter);
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });
    }


    @SuppressLint("NonConstantResourceId")
    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), DashboardMeteActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_scan:
                    startActivity(new Intent(getApplicationContext(), QrCodeScanner.class));
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

        getMenuInflater().inflate(R.menu.percorso_top_menu, menu);

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:

                Percorso percorso = getIntent().getExtras().getParcelable("percorso");

                Intent intent =new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Vieni a vedere il "+percorso.getNome()+"\n\n"+"\n\n"+percorso.getDescrizione()+"\n\n"+ "valutazione media:"+mediaStelle+"/5"+"\n\n"+"Scaricati l'app ECulture-Experience!");

                startActivity(Intent.createChooser(intent,"Condividi il luogo di interesse"));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }


    }

    /**
     * Funzione atomica di scrittura su db dato un map<string, object> e la collectionPath
     */
    public void addDoc(Map<String, Object> valutazione, String collectionPath){

        db.collection(collectionPath)
                .add(valutazione)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

    /**
     * Funzione atomica di eliminazione di un documento su db attraverso la collectinoPath e l'id del documento da eliminare
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
     */
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
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
                            float media = (float) sommaValutazioni/numeroVotanti;
                            mediaStelle = (float) sommaValutazioni / numeroVotanti;


                            mediaValutazione.setText(String.format("%.02f", media));
                            if(numeroVotanti > 1){
                                numeroVotazioni.setText( numeroVotanti +" "+  getString(R.string.reviews));
                            }
                            else{
                                numeroVotazioni.setText( numeroVotanti +" "+  getString(R.string.review));
                            }


                        }
                    }
                });

    }

    /**
     * Apre un nuovo Dialog che permette all'utente di inserire una valutazione attraverso il ratingBar a stella
     */
    public void writeRating(String collectionPathValutazione){

        Dialog rankDialog = new Dialog(MostraPercorsoActivity.this, R.style.Base_Theme_AppCompat_Dialog_Alert);
        rankDialog.setContentView(R.layout.rank_dialog);
        rankDialog.setCancelable(true);

        RatingBar ratingStarsDialog = rankDialog.findViewById(R.id.ratingBarDialog);
        //Setting iniziale del ratingStars con il voto precedentemente inserito
        letturaValutazione(collectionPathValutazione, ratingStarsDialog);
        //Scrittura e aggiornamento del voto
        ratingStarsDialog.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            //Se la valutazione data è maggiore di 0 (ha votato), allora salvo la valutazione in un contenitore unico per tutti gli utenti
            if(rating > 0){
                scritturaValutazioneDatabase(collectionPathValutazione, rating);
            }
        });

        TextView titoloDialog = rankDialog.findViewById(R.id.titleRating);
        titoloDialog.setText("Valuta il percorso");

        Button updateButton = rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(v -> {
            Toast.makeText(this,"Hai valutato correttamente il percorso",Toast.LENGTH_SHORT).show();
            rankDialog.dismiss();
        });


        rankDialog.show();

    }

    public void setColorButtons(){
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_share_24);
        Drawable unwrappedDrawable2 = AppCompatResources.getDrawable(this, R.drawable.toggle_selector);
        assert unwrappedDrawable != null;
        assert unwrappedDrawable2 != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        Drawable wrappedDrawable2 = DrawableCompat.wrap(unwrappedDrawable2);
        DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
        DrawableCompat.setTint(wrappedDrawable2, Color.RED);
    }
}