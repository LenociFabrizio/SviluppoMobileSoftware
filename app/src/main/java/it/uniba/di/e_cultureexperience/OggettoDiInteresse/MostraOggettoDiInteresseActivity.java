package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.QRScanner.QRScanner;
import it.uniba.di.e_cultureexperience.QuizGame.DashboardActivity;
import it.uniba.di.e_cultureexperience.QuizGame.PuzzleGame;
import it.uniba.di.e_cultureexperience.QuizGame.QuesitoQuiz;
import it.uniba.di.e_cultureexperience.R;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.QRScanner.QRScanner;
import it.uniba.di.e_cultureexperience.QuizGame.DashboardActivity;
import it.uniba.di.e_cultureexperience.QuizGame.PuzzleGame;
import it.uniba.di.e_cultureexperience.QuizGame.QuesitoQuiz;
import it.uniba.di.e_cultureexperience.R;

public class MostraOggettoDiInteresseActivity extends AppCompatActivity {
    private TextView descrizioneOggetto, bluetoothOggetto;
    private ImageView immagineOggetto;
    private Button quizBtn, puzzleBtn;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oggetto_di_interesse);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //Prendo l'oggetto passato dall'intent
        OggettoDiInteresse oggettoDiInteresse = getIntent().getExtras().getParcelable("oggettoDiInteresse");

        descrizioneOggetto = findViewById(R.id.descrizioneTxt);
        immagineOggetto = findViewById(R.id.immagineOggetto);
        bluetoothOggetto  = findViewById(R.id.bluetoothIdTxt);
        quizBtn = findViewById(R.id.btn_quiz);
        puzzleBtn = findViewById(R.id.btn_puzzleGame);


        Picasso.with(this)
                .load(oggettoDiInteresse.getUrl_immagine())
                .into(immagineOggetto);

        descrizioneOggetto.setText(oggettoDiInteresse.getDescrizione());

        bluetoothOggetto.setText(oggettoDiInteresse.getBluetooth_id());

        Toolbar mToolbar = findViewById(R.id.toolbar_oggettodiinteresse);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(oggettoDiInteresse.getNome());

        CollapsingToolbarLayout collapsingLayout = findViewById(R.id.collapsing_toolbar);
        collapsingLayout.setExpandedTitleColor(Color.parseColor("#ffffff"));
        collapsingLayout.setCollapsedTitleTextColor(Color.parseColor("#000000"));

        //controllo se l' oggetto ha un quiz
        db.collection("/oggetti/"+oggettoDiInteresse.getId()+"/quesiti_quiz")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        quizBtn.setVisibility(View.VISIBLE);
                        //quando clicca sul bottone gli passo l' array contenente i quesiti
                        ArrayList<QuesitoQuiz> quesiti = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuesitoQuiz temp = document.toObject(QuesitoQuiz.class);
                            quesiti.add(temp);
                        }
                        quizBtn.setOnClickListener(v -> {

                            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("quesiti", quesiti);
                            i.putExtra("idOggetto", oggettoDiInteresse.getId());
                            i.putExtra("url",oggettoDiInteresse.getUrl_immagine());
                            getApplicationContext().startActivity(i);
                        });
                    } else {
                        Log.w("Error", "Non ha nessun quiz", task.getException());
                    }
                });


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
        bottomNav.setSelectedItemId(R.id.share);

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
}