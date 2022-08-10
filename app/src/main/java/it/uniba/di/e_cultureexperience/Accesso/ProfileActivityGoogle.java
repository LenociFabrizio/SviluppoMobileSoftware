package it.uniba.di.e_cultureexperience.Accesso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.R;

public class ProfileActivityGoogle extends AppCompatActivity {

    private ImageView immagineProfilo;
    private TextView eMailGoogle;
    private TextView esciDalProfilo;
    private TextView nickname;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView email = findViewById(R.id.emailView);
        fAuth = FirebaseAuth.getInstance();
        immagineProfilo = findViewById(R.id.profileImage);
        nickname = findViewById(R.id.nicknameView);

        //setContentView(R.layout.activity_google_login);
        Map<String, Object> utente = new HashMap<>();
        // scritturaDataBase(utente,eMail);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            Uri googleAccountImagePh = account.getPhotoUrl();

            Glide.with(this)
                    .load(googleAccountImagePh).into(immagineProfilo);

            email.setText(account.getEmail());
        }

        onCreateBottomNavigation();
    }

    private void onCreateBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_profile);
        //Perform ItemSelectedListener
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            //mi carico il segnalino dal first activity
            boolean loginGoogle = getIntent().getExtras().getBoolean("segnalino");
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), DashboardMete.class).putExtra("segnalino",loginGoogle));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_scan:
                        startActivity(new Intent(getApplicationContext(), FirstAccessActivity.class).putExtra("segnalino",loginGoogle));
                        overridePendingTransition(0,0);
                        return true;
                    //selectedFragment = new ScanFragment();
                    //break;
                    case R.id.nav_profile:
                        return true;

                }
                return false;
            }
        });
    }
}