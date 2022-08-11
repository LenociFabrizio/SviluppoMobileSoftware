package it.uniba.di.e_cultureexperience.Accesso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("studenti");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView email = findViewById(R.id.emailView);
        fAuth = FirebaseAuth.getInstance();
        immagineProfilo = findViewById(R.id.profileImage);
        nickname = findViewById(R.id.nicknameView);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);

        String emailPassaggio=account.getEmail();
        String[] nicknamePassaggio=emailPassaggio.split("@");

        System.out.println("0000000 "+nicknamePassaggio[0] + " " + nicknamePassaggio[1]);

        Utente u =new Utente(nicknamePassaggio[0],"123456",emailPassaggio);
        mDatabase.push().setValue(u);
        Toast.makeText(ProfileActivityGoogle.this, "aggiunto!?", Toast.LENGTH_LONG).show();



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


    public void changePassw(View v){
        Intent i = new Intent(ProfileActivityGoogle.this, ForgotPasswordActivity.class);
        startActivity(i);
    }

}