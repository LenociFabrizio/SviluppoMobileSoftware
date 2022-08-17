package it.uniba.di.e_cultureexperience.Accesso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.R;

public class ProfileActivityGoogle extends AppCompatActivity {

    private ImageView immagineProfilo;
    private TextView nickname;

    //metodi per il login google
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    //informazioni db
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView emailGoogle = findViewById(R.id.emailView);
        immagineProfilo = findViewById(R.id.profileImage);
        nickname = findViewById(R.id.nicknameView);
        //Button esciDalProfilo = findViewById(R.id.changePasswordView);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);

        String emailPassaggio=account.getEmail();
        String[] nicknamePassaggio=emailPassaggio.split("@");

        /*
        esciDalProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                esci();
            }
        });
        */
        Uri googleAccountImagePh = account.getPhotoUrl();

        Glide.with(this)
                .load(googleAccountImagePh).into(immagineProfilo);

        emailGoogle.setText(account.getEmail());

        //Ricercare il proprio nickname con idDB == idLocale
        db.collection("utenti")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String nicknameDataBase = document.getString("nickname");
                                nicknameDataBase.toLowerCase();

                                if (nicknameDataBase.equals(nicknamePassaggio[0])) {
                                    nickname.setText(nicknameDataBase);
                                    return;

                                } else {
                                    Log.d("ID NON TROVATO", "ERROR"+ nicknameDataBase);
                                }
                            }
                        } else {
                            Log.d("DB VUOTO", "ERROR");
                        }
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }

                });

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
                        startActivity(new Intent(getApplicationContext(), ProfileActivityGoogle.class).putExtra("segnalino",loginGoogle));
                        overridePendingTransition(0,0);
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

    public void signOut(View v){
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();

        GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(this,gso);
        googleSignInClient.signOut();

        Intent i = new Intent(ProfileActivityGoogle.this, FirstAccessActivity.class);
        startActivity(i);
        finish();
    }

}