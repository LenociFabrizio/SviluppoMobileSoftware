package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.LuogoDiInteresse;
import it.uniba.di.e_cultureexperience.QRScanner.QRScanner;
import it.uniba.di.e_cultureexperience.R;
import it.uniba.di.e_cultureexperience.QuizGame.QuesitoQuiz;
import it.uniba.di.e_cultureexperience.QuizGame.PuzzleGame;
import it.uniba.di.e_cultureexperience.QuizGame.DashboardActivity;

public class MostraOggettoDiInteresseActivity extends AppCompatActivity {
    private TextView descrizioneOggetto, bluetoothOggetto;
    private ImageView immagineOggetto;
    private Button quizBtn, puzzleBtn;
    private RatingBar ratingStars;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String collectionPath;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oggetto_di_interesse);

        //Prendo l'oggetto passato dall'intent
        OggettoDiInteresse oggettoDiInteresse = getIntent().getExtras().getParcelable("oggettoDiInteresse");

        collectionPath = "oggetti/" + oggettoDiInteresse.getId() + "/valutazione";

        ratingStars = findViewById(R.id.ratingBar);
        descrizioneOggetto = findViewById(R.id.descrizioneTxt);
        immagineOggetto = findViewById(R.id.immagineOggetto);
        bluetoothOggetto  = findViewById(R.id.bluetoothIdTxt);
        quizBtn = findViewById(R.id.btn_quiz);
        puzzleBtn = findViewById(R.id.btn_puzzleGame);

        letturaValutazione(collectionPath);
        //S T A R T - Rating stars
        ratingStars.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            /**
             * Se la valutazione data è maggiore di 0 (ha votato), allora salvo la valutazione in un contenitore unico per tutti gli utenti così da poter fare una media
             */
            if(rating > 0){
                scritturaValutazioneDatabase(collectionPath, rating);
            }
        });

        //S T A R T - set content into layout
        Picasso.with(this)
                .load(oggettoDiInteresse.getUrl_immagine())
                .into(immagineOggetto);

        descrizioneOggetto.setText(oggettoDiInteresse.getDescrizione());

        bluetoothOggetto.setText(oggettoDiInteresse.getBluetooth_id());

        Toolbar mToolbar = findViewById(R.id.toolbar_oggettodiinteresse);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(oggettoDiInteresse.getNome());

        CollapsingToolbarLayout collapsingLayout = findViewById(R.id.collapsing_toolbar);
        collapsingLayout.setExpandedTitleColor(Color.parseColor(getResources().getString(R.color.white)));
        collapsingLayout.setCollapsedTitleTextColor(Color.parseColor(getResources().getString(R.color.black)));

        //controllo se l' oggetto ha un quiz
        db.collection("/oggetti/"+oggettoDiInteresse.getId()+"/quesiti_quiz")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //ha un quiz, rendo visibile il bottone del quiz

                            quizBtn.setVisibility(View.VISIBLE);
                            //quando clicca sul bottone gli passo l' array contenente i quesiti
                            ArrayList<QuesitoQuiz> quesiti = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                QuesitoQuiz temp = document.toObject(QuesitoQuiz.class);
                                quesiti.add(temp);
                            }
                            quizBtn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //quando viene premuto, lancia l' intent esplicito
                                    Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("url",oggettoDiInteresse.getUrl_immagine());
                                    i.putExtra("idOggetto", oggettoDiInteresse.getId());
                                    i.putExtra("quesiti", quesiti);
                                    getApplicationContext().startActivity(i);
                                }
                            });
                        } else {
                            //non ha nessun quiz, rimane invisibile
                            Log.w("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                        }
                    }
                });

        //controllo se l'oggetto ha un puzzle
        db.collection("/oggetti/")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        puzzleBtn.setVisibility(View.VISIBLE);
                        puzzleBtn.setOnClickListener(v -> {
                            Intent i = new Intent(getApplicationContext(), PuzzleGame.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("urlImmagine", oggettoDiInteresse.getUrl_immagine());
                            getApplicationContext().startActivity(i);
                        });
                    } else {
                        Log.w("Error", "Lettura non avvenua url_immagine oggetto", task.getException());
                    }
                });

        onCreateBottomNavigation();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
        return super.onSupportNavigateUp();
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
    public void letturaValutazione(String collectionPath){

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
                                    ratingStars.setRating((float) valutazione);
                                }
                            }//fine for
                        }
                    }
                });
    }

    /**
     * Calcolo della media valutazione per un determinato oggetto di interesse
     * @param collectionPath
     */
    public void calcoloMediaValutazione(String collectionPath){

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        int sizeDataBase = task.getResult().size();
                        if (sizeDataBase != 0) {
                            double sommaValutazioni = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                double valutazione = document.getDouble("valutazione");

                                sommaValutazioni = valutazione + sommaValutazioni;
                            }//fine for
                            float mediaValutazione = (float) sommaValutazioni/sizeDataBase;
                        }
                    }
                });
    }


}