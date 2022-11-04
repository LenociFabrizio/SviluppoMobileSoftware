package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.di.e_cultureexperience.R;


public class RegistrationActivity extends AppCompatActivity {

    private static final int MIN_LENGTH_NICKNAME = 3, MIN_LENGTH_PASSWORD = 8;

    private TextInputEditText editTextNickname, editTextPassword, editTextEmail;
    private TextInputLayout layoutNickname, layoutPassword, layoutEmail;

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Gestione Nickname
        editTextNickname = findViewById(R.id.nicknameInputEditText);
        layoutNickname = findViewById(R.id.nicknameTextInputLayout);

        editTextNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nickname = charSequence.toString().trim();
                if(nickname.length() < MIN_LENGTH_NICKNAME){
                    layoutNickname.setHelperText("Inserisci almeno 3 caratteri");
                    layoutNickname.setError("");
                    layoutNickname.requestFocus();
                    return;
                }else{

                    //Controllo di pre-esistenza del Nickname nel database
                    db.collection("utenti")
                            .get()
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    boolean nicknameTrovato = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        String nicknameDataBase = document.getString("nickname");

                                        if(nicknameDataBase.toLowerCase().equals(nickname.toLowerCase()))
                                            nicknameTrovato = true;

                                    }

                                    if(nicknameTrovato){
                                        layoutNickname.setError("Nickname già esistente!");
                                        layoutNickname.requestFocus();
                                        return;
                                    }else{
                                        layoutNickname.setHelperText("Nickname valido");
                                        layoutNickname.setError("");
                                    }

                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Gestione Email
        editTextEmail = findViewById(R.id.emailInputEditText);
        layoutEmail = findViewById(R.id.emailTextInputLayout);

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = charSequence.toString();
                final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

                if(!matcher.find() || email.isEmpty()){
                    layoutEmail.setHelperText("");
                    layoutEmail.setError("Inserisci una email valida");
                    layoutEmail.requestFocus();
                    return;
                }else{
                    layoutEmail.setHelperText("Email inserita valida");
                    layoutEmail.setError("");
                }

                //Controllo se email inserita esiste nel database
                fAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                    boolean check = !task.getResult().getSignInMethods().isEmpty();

                    if(check){
                        layoutEmail.setHelperText("");
                        layoutEmail.setError("Esiste questa email");
                        layoutEmail.requestFocus();
                        return;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Gestione Password
        editTextPassword = findViewById(R.id.passwordInputEditText);
        layoutPassword = findViewById(R.id.passwordTextInputLayout);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();

                if (password.length() >= MIN_LENGTH_PASSWORD){
                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher matcher = pattern.matcher(password);
                    boolean isPasswordContainsSpeChar = matcher.find();
                    if(isPasswordContainsSpeChar){
                        layoutPassword.setHelperText("Password forte");
                        layoutPassword.setError("");
                    }else{
                        layoutPassword.setHelperText("");
                        layoutPassword.setError("Password debole, inserisci minimo un carattere speciale");
                        layoutPassword.requestFocus();
                        return;
                    }
                }else{
                    layoutPassword.setHelperText("Inserisci minimo " + MIN_LENGTH_PASSWORD + " caratteri");
                    layoutPassword.setError("");
                    layoutPassword.requestFocus();
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        TextView loginText = findViewById(R.id.loginTextView);
        loginText.setOnClickListener(view -> {
            //todo: cambiare context
            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(i);
        });
    }

    /**
     * Scrittura di nickname e di id Firebase nella path assegnata agli utenti nel firebase firestore db
     */
    public void scritturaUtenteDataBase(String nickname){
        Map<String, Object> utente = new HashMap<>();

        utente.put("idUtente", fAuth.getUid());
        utente.put("nickname", nickname);

        db.collection("utenti")
                .add(utente)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }


    public void registerBtnClick(View v){

        String email = editTextEmail.getText().toString(), password = editTextPassword.getText().toString(), nickname = editTextNickname.getText().toString().trim();
        //Controllo inserimento NICKNAME prima della registrazione
        if(nickname.length() < MIN_LENGTH_NICKNAME){
            layoutNickname.setHelperText("Inserisci almeno 3 caratteri");
            layoutNickname.setError("");
            layoutNickname.requestFocus();
            return;
        }else{

            //Controllo di pre-esistenza del Nickname nel database
            db.collection("utenti")
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            boolean nicknameTrovato = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String nicknameDataBase = document.getString("nickname");

                                if(nicknameDataBase.toLowerCase().equals(nickname.toLowerCase()))
                                    nicknameTrovato = true;

                            }

                            if(nicknameTrovato){
                                layoutNickname.setError("Nickname già esistente!");
                                layoutNickname.requestFocus();
                                return;
                            }else{
                                layoutNickname.setHelperText("Nickname valido");
                                layoutNickname.setError("");
                            }

                        }
                    });
        }

        //Controllo inserimento EMAIL prima della registrazione
        final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

        if(!matcher.find() || email.isEmpty()){
            layoutEmail.setHelperText("");
            layoutEmail.setError("Inserisci una email valida");
            layoutEmail.requestFocus();
            return;
        }else{
            layoutEmail.setHelperText("Email inserita valida");
            layoutEmail.setError("");
        }

        //Controllo se email inserita esiste nel database
        AtomicBoolean emailEsistente = new AtomicBoolean(false);
        fAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            boolean check = !task.getResult().getSignInMethods().isEmpty();

            if(check){
                emailEsistente.set(true);
                layoutEmail.setHelperText("");
                layoutEmail.setError("Esiste questa email");
                layoutEmail.requestFocus();
                return;
            }
        });

        //Controllo inserimento PASSWORD prima della registrazione
        if (password.length() >= MIN_LENGTH_PASSWORD){
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
            Matcher matcherPsw = pattern.matcher(password);
            boolean isPasswordContainsSpeChar = matcherPsw.find();
            if(isPasswordContainsSpeChar){
                layoutPassword.setHelperText("Password forte");
                layoutPassword.setError("");
            }else{
                layoutPassword.setHelperText("");
                layoutPassword.setError("Password debole, inserisci minimo un carattere speciale");
                layoutPassword.requestFocus();
                return;
            }
        }else{
            layoutPassword.setHelperText("Inserisci minimo " + MIN_LENGTH_PASSWORD + " caratteri");
            layoutPassword.setError("");
            layoutPassword.requestFocus();
            return;
        }


        (fAuth.createUserWithEmailAndPassword(email, password)).addOnCompleteListener(task -> {

            final boolean[] nicknameDuplicato = {false}, nicknameCorto = {false};

            db.collection("utenti")
                    .get()
                    .addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task1.getResult()) {

                                String nicknameDataBase = document.getString("nickname");
                                if(nicknameDataBase.toLowerCase().equals(nickname.toLowerCase())) {
                                    nicknameDuplicato[0] = true;
                                    return;
                                }

                                if(nickname.length() < MIN_LENGTH_NICKNAME){
                                    nicknameCorto[0] = true;
                                    return;
                                }

                            }//Fine for

                            if(!nicknameDuplicato[0] && !nicknameCorto[0] && !emailEsistente.get()){


                                scritturaUtenteDataBase(nickname);
                                //Todo: modificare context
                                Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                                //Todo: togliere commenti
                                //Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);    startActivity(i);
                            }

                        }else
                            Log.d("Error", "to reading database values");}
                    );
        });
    }

}