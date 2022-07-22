package it.uniba.di.e_cultureexperience.Accesso;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.R;

public class HomeGoogleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final TextView email=findViewById(R.id.Nome);
        final TextView nome=findViewById(R.id.Email);
        @SuppressLint("WrongViewCast") final AppCompatButton signOutBtn = findViewById(R.id.signOutBtn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount googleSignInAccount=GoogleSignIn.getLastSignedInAccount(this);


        if (googleSignInAccount != null) {
            //Dettagli dell'utente loggato
            final String getFullName=googleSignInAccount.getDisplayName();
            final String getEmail=googleSignInAccount.getEmail();

            email.setText("Email: "+getEmail);
            nome.setText("Nome: "+getFullName);
            //startActivity(new Intent(LoginGoogle.this, MainActivity.class));
            //finish();
        }


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SignOut
                googleSignInClient.signOut();
                //Apri la main Activity
                startActivity(new Intent(HomeGoogleActivity.this, DashboardMete.class));
                finish();
            }
        });
    }
}