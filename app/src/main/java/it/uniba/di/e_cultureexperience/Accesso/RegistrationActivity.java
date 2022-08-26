package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.e_cultureexperience.R;


public class RegistrationActivity extends AppCompatActivity {

    private EditText txtEmailRegistration, txtPasswordRegistration, txtNickname;

    private FirebaseAuth fAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtEmailRegistration = findViewById(R.id.emailRegistration);
        txtPasswordRegistration = findViewById(R.id.passwordRegistration);
        txtNickname = findViewById(R.id.nicknameRegistration);
        TextView txtLogin = findViewById(R.id.loginTxt);
        fAuth = FirebaseAuth.getInstance();

        txtLogin.setOnClickListener(v -> goToLogin());
    }

    public void registerBtnClick(View v){
        String email = txtEmailRegistration.getText().toString().trim();
        String password = txtPasswordRegistration.getText().toString().trim();
        String nickname = txtNickname.getText().toString().trim();

        //Controllo email vuota
        if(email.isEmpty()){
            txtEmailRegistration.setError("Email richiesta!");
            txtEmailRegistration.requestFocus();
            return; }
        //Controllo password vuota
        if(password.isEmpty()){
            txtPasswordRegistration.setError("Password richiesta!");
            txtPasswordRegistration.requestFocus();
            return; }
        //controllo nickname vuoto
        if(nickname.isEmpty()){
            txtNickname.setError("Nickname richiesto!");
            txtNickname.requestFocus();
            return; }//se il nickname è stato inserito allora vado avanti

        //controllo se il nickname è stato già inserito
        db.collection("utenti")
                .get()
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful()) {

                            final int sizeDataBase = task.getResult().size();

                            if(sizeDataBase==0){
                                Toast.makeText(RegistrationActivity.this, "Sei il primo utente della nostra APP!", Toast.LENGTH_LONG).show();
                            }else{
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String nicknameDataBase = document.getString("nickname");
                                    nicknameDataBase.toLowerCase(); nickname.toLowerCase();

                                    if(nicknameDataBase.equals(nickname)){
                                        txtNickname.setError("Nickname già esistente!");
                                        txtNickname.requestFocus();
                                        return;
                                    }

                                    if(nickname.length() > 10){
                                        txtNickname.setError("Massimo 10 caratteri!");
                                        txtNickname.requestFocus();
                                        return;
                                    }
                                }
                            }
                    }
                });

        //todo: controllo email uguale

        (fAuth.createUserWithEmailAndPassword(email, password)).addOnCompleteListener(task -> {

            //Controllo email vuota
            if(email.isEmpty()){
                txtEmailRegistration.setError("Email richiesta!");
                txtEmailRegistration.requestFocus();
                return;
            }
            //Controllo password vuota
            if(password.isEmpty()){
                txtPasswordRegistration.setError("Password richiesta!");
                txtPasswordRegistration.requestFocus();
                return;
            }
            //controllo nickname vuoto
            if(nickname.isEmpty()){
                txtNickname.setError("Nickname richiesto!");
                txtNickname.requestFocus();
                return;
            }
            //controllo nickname uguale
            final boolean[] nicknameDuplicato = {false}, nicknameLungo = {false};

            db.collection("utenti")
                    .get()
                    .addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {
                            //se il db è vuoto
                            int sizeDataBase = task1.getResult().size();
                            if(sizeDataBase == 0){

                                Toast.makeText(RegistrationActivity.this, "Sei il primo utente della nostra APP!", Toast.LENGTH_LONG).show();
                                Map<String, Object> utente = new HashMap<>();
                                scritturaDataBase(utente, nickname);

                                if(task1.isSuccessful()){
                                    Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    startActivity(i);
                                }else{
                                    if(txtEmailRegistration.getText().toString().equals("") || txtPasswordRegistration.getText().toString().equals("") || txtNickname.getText().toString().equals("")){
                                        Toast.makeText(RegistrationActivity.this, "Inserisci tutti i dati!", Toast.LENGTH_LONG).show();
                                    }else {
                                        Log.e("ERROR", task1.getException().toString());
                                        Toast.makeText(RegistrationActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                            }else{//se il db non è vuoto
                                int count = 0;

                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    count++;
                                    String nicknameDataBase = document.getString("nickname");

                                    if(nickname.length() > 10){
                                        txtNickname.setError("Massimo 10 caratteri!");
                                        txtNickname.requestFocus();
                                        nicknameLungo[0] = true;
                                        return;
                                    }

                                    if(nicknameDataBase.equals(nickname)){

                                        txtNickname.setError("Nickname già esistente!");;
                                        txtNickname.requestFocus();

                                        nicknameDuplicato[0] = true;

                                        return;

                                    }else{//se sono arrivato all'ultimo

                                        if(count == sizeDataBase && nicknameDuplicato[0] == false && nicknameLungo[0] == false){

                                            Map<String, Object> utente = new HashMap<>();
                                            scritturaDataBase(utente, nickname);

                                            if(task1.isSuccessful()){
                                                Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_LONG).show();

                                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                startActivity(i);
                                            }else{
                                                if(txtEmailRegistration.getText().toString().equals("") || txtPasswordRegistration.getText().toString().equals("") || txtNickname.getText().toString().equals("")){
                                                    Toast.makeText(RegistrationActivity.this, "Inserisci tutti i dati!", Toast.LENGTH_LONG).show();
                                                }else {
                                                    Toast.makeText(RegistrationActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
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

    public void goToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}