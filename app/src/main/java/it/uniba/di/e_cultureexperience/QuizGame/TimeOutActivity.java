package it.uniba.di.e_cultureexperience.QuizGame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.QrCodeScanner;
import it.uniba.di.e_cultureexperience.R;

public class TimeOutActivity extends AppCompatActivity {
    Button retryBtn, exitBtn;
    private ImageView immagineOggetto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        // = findViewById(R.id.retryButton);
        exitBtn = findViewById(R.id.exitButton);

        immagineOggetto = findViewById(R.id.immagine);

        //carico url immagine e la faccio vedere a schermo
        String urlImmagineOggetto = getIntent().getExtras().getString("url");

        Picasso.with(this)
                .load(urlImmagineOggetto)
                .into(immagineOggetto);

        retryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TimeOutActivity.this, QuizGameActivity.class);
            intent.putExtra("url",urlImmagineOggetto);
            startActivity(intent);
            finish();
        });
        exitBtn.setOnClickListener(v -> {
            GradientDrawable bgShape1 = (GradientDrawable) exitBtn.getBackground();
            bgShape1.setColor(Color.parseColor("#000000"));
            exitBtn.setTextColor(Color.parseColor("#ffffff"));


            Intent intent = new Intent(TimeOutActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        onCreateBottomNavigation();
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
}