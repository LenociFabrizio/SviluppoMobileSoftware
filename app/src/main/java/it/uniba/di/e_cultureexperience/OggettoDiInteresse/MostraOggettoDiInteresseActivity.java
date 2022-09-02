package it.uniba.di.e_cultureexperience.OggettoDiInteresse;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.R;
import it.uniba.di.e_cultureexperience.QuizGame.QuesitoQuiz;
import it.uniba.di.e_cultureexperience.QuizGame.DashboardActivity;

public class MostraOggettoDiInteresseActivity extends AppCompatActivity {
    private TextView descrizioneOggetto, bluetoothOggetto;
    private ImageView immagineOggetto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oggetto_di_interesse);

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
        Log.d("OggettoDiInteresse => ", oggettoDiInteresse.toString());


        descrizioneOggetto = findViewById(R.id.descrizioneTxt);
        immagineOggetto = findViewById(R.id.immagineOggetto);
        bluetoothOggetto  = findViewById(R.id.bluetoothIdTxt);

        //S T A R T - set content into layout
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

        //OGGETTI PER FIREBASE
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //controllo se l' oggetto ha un quiz
        db.collection("/oggetti/"+oggettoDiInteresse.getId()+"/quesiti_quiz")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //ha un quiz, rendo visibile il bottone del quiz
                        Button button = findViewById(R.id.btn_quiz);
                        button.setVisibility(View.VISIBLE);
                        //quando clicca sul bottone gli passo l' array contenente i quesiti
                        ArrayList<QuesitoQuiz> quesiti = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuesitoQuiz temp = document.toObject(QuesitoQuiz.class);
                            quesiti.add(temp);
                        }
                        button.setOnClickListener(v -> {
                            //quando viene premuto, lancia l' intent esplicito
                            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                            i.putExtra("quesiti", quesiti);
                            getApplicationContext().startActivity(i);
                        });
                    } else {
                        //non ha nessun quiz, rimane invisibile
                        Log.w("ENDRIT", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });
        //F I N I S H

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
}