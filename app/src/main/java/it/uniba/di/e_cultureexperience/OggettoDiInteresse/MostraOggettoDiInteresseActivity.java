package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;
import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.QRScanner.QRScanner;
import it.uniba.di.e_cultureexperience.R;
import it.uniba.di.e_cultureexperience.QuizGame.QuesitoQuiz;
import it.uniba.di.e_cultureexperience.QuizGame.PuzzleGame;
import it.uniba.di.e_cultureexperience.QuizGame.DashboardActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MostraOggettoDiInteresseActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button quizBtn, puzzleBtn;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oggetto_di_interesse);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        //Prendo l'oggetto passato dall'intent
        Intent intent = getIntent();
        OggettoDiInteresse oggettoDiInteresse = intent.getExtras().getParcelable("oggettoDiInteresse");
        boolean scannerizzato = intent.getBooleanExtra("scannerizzato", false);

        TextView descrizioneOggetto = findViewById(R.id.descrizioneTxt);
        ImageView immagineOggetto = findViewById(R.id.immagineOggetto);
        TextView bluetoothOggetto = findViewById(R.id.bluetoothIdTxt);
        quizBtn = findViewById(R.id.btn_quiz);
        puzzleBtn = findViewById(R.id.btn_puzzleGame);

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
                            if(task.getResult().size() > 0 ) {
                                //ha un quiz, rendo visibile il bottone del quiz(se è stato scannerizzato)
                                quizBtn.setVisibility(View.VISIBLE);
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
                            }
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
                        if(task.getResult().size() > 0) {
                            //ha un puzzle, rendo visibile il bottone del puzzle(se è stato scannerizzato)
                            puzzleBtn.setVisibility(View.VISIBLE);
                            puzzleBtn.setOnClickListener(v -> {
                                Intent i = new Intent(getApplicationContext(), PuzzleGame.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("urlImmagine", oggettoDiInteresse.getUrl_immagine());
                                getApplicationContext().startActivity(i);
                            });
                        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent = getIntent();
        OggettoDiInteresse oggettoDiInteresse = intent.getExtras().getParcelable("oggettoDiInteresse");
        LatLng coordinate = new LatLng(oggettoDiInteresse.getLatitudine(), oggettoDiInteresse.getLongitudine());
        googleMap.addMarker(new MarkerOptions()
                .position(coordinate)
                .title(oggettoDiInteresse.getNome()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
    }
}