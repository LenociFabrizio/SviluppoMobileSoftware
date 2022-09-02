package it.uniba.di.e_cultureexperience.Accesso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.R;

public class LoginGoogleActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private FirebaseAuth fAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login_google);

        //LOGIN CLASSICO
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc= GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);

        //SETTO DI DEFAULT LE INFORMAZIONI DELL'ACCOUNT LOGGATO CON GOOGLE
        String emailPassaggio=account.getEmail();
        String[] nicknamePassaggio=emailPassaggio.split("@");
        String passwordDefault="000000";

        fAuth = FirebaseAuth.getInstance();

        //controllo se il nickname è stato già inserito NEL DB
        db.collection("utenti")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        final int sizeDataBase = task.getResult().size();
                        //DATABASE VUOTO
                        if(sizeDataBase==0){
                            Toast.makeText(LoginGoogleActivity.this, "Sei il primo utente della nostra APP!", Toast.LENGTH_LONG).show();
                        }else{//DATABASE NON VUOTO
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String nicknameDataBase = document.getString("nickname");
                                nicknameDataBase.toLowerCase();

                                // SE IL NICKNAME ESISTE
                                if(nicknameDataBase.equals(nicknamePassaggio[0])){
                                    //NICKNAME GIà ESISTENTE
                                    boolean loginGoogle = getIntent().getExtras().getBoolean("segnalino");
                                    Intent i = new Intent(LoginGoogleActivity.this, DashboardMeteActivity.class).putExtra("segnalino",loginGoogle);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                });

        // CONTROLLO SE L'UTENTE LOGGATO CON GOOGLE è REGISTRATO NEL NOSTRO DB, SE NON è REGISTRATO LO REGISTRA
        // ASSEGNANDOLI I VALORI DI DEFAULT SOPRA DICHIARATI
        (fAuth.createUserWithEmailAndPassword(emailPassaggio, passwordDefault)).addOnCompleteListener(task -> {

            //controllo nickname uguale
            final boolean[] nicknameDuplicato = {false}, nicknameLungo = {false};

            db.collection("utenti")
                    .get()
                    .addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {
                            //se il db è vuoto
                            int sizeDataBase = task1.getResult().size();
                            if(sizeDataBase == 0){

                                Toast.makeText(LoginGoogleActivity.this, "Sei il primo utente della nostra APP!", Toast.LENGTH_LONG).show();
                                Map<String, Object> utente = new HashMap<>();
                                scritturaDataBase(utente, nicknamePassaggio[0]);

                                if(task1.isSuccessful()){
                                    Toast.makeText(LoginGoogleActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(LoginGoogleActivity.this, ProfileActivityGoogle.class);
                                    startActivity(i);
                                }else{
                                    Log.e("ERROR", task1.getException().toString());
                                    Toast.makeText(LoginGoogleActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }else{//se il db non è vuoto
                                int count = 0;

                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    count++;
                                    String nicknameDataBase = document.getString("nickname");

                                    if(nicknameDataBase.equals(nicknamePassaggio[0])){

                                        nicknameDuplicato[0] = true;

                                        return;

                                    }else{//se sono arrivato all'ultimo

                                        if(count == sizeDataBase && nicknameDuplicato[0] == false && nicknameLungo[0] == false){

                                            Map<String, Object> utente = new HashMap<>();
                                            scritturaDataBase(utente, nicknamePassaggio[0]);

                                            if(task1.isSuccessful()){
                                                Toast.makeText(LoginGoogleActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                                                Intent i = new Intent(LoginGoogleActivity.this, ProfileActivityGoogle.class);
                                                startActivity(i);
                                            }else{
                                                Toast.makeText(LoginGoogleActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    }
                                }}
                        }
                    });


        });

    }

    public void scritturaDataBase(Map<String, Object> utente, String nickname){

        utente.put("idUtente", fAuth.getUid());
        utente.put("nickname", nickname);
        //scrittura
        db.collection("utenti")
                .add(utente)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

}