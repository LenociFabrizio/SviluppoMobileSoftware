package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.R;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextPassword, editTextEmail;
    private TextInputLayout layoutPassword, layoutEmail;
    private Button registrationBtn;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.emailInputEditText);
        layoutEmail = findViewById(R.id.emailTextInputLayout);

        editTextPassword = findViewById(R.id.passwordInputEditText);
        layoutPassword = findViewById(R.id.passwordTextInputLayout);

        registrationBtn = findViewById(R.id.registrationBtn);
        registrationBtn.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(i);
        });

        CheckBox rememberBox = findViewById(R.id.rememberMe);


        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");

        if(checkbox.equals("true")){
            Intent intent = new Intent(LoginActivity.this, it.uniba.di.e_cultureexperience.Bluetooth.MainActivity.class);
            startActivity(intent);
            finish();
        }

        rememberBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(buttonView.isChecked()){

                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember", "true");
                editor.apply();

            }else if(!buttonView.isChecked()){

                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember", "false");
                editor.apply();

            }

        });

        TextView registerText = findViewById(R.id.registrationBtn);
        registerText.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(i);
        });
    }

    public void loginBtnClick(View view) {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (emptyCheck(email, layoutEmail, "Inserisci email d'accesso")) return;

        //Controllo password vuota
        if (emptyCheck(password, layoutPassword, "Inserisci password d'accesso")) return;

        (fAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                Toast.makeText(LoginActivity.this, "Login effettuato", Toast.LENGTH_LONG).show();

                Intent i = new Intent(LoginActivity.this, DashboardMeteActivity.class);
//                Intent i = new Intent(LoginActivity.this, it.uniba.di.e_cultureexperience.Bluetooth.MainActivity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean emptyCheck(String string, TextInputLayout layout, String s) {

        if (string.isEmpty()) {
            layout.setError(s);
            layout.requestFocus();
            return true;
        } else {
            layout.setError("");
        }
        return false;
    }

    public void forgotTxtClick(View v){
        Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(i);
    }

    /**
     * Permette di accedere senza inserimento di email e password. Crea un utente anonimo in Firebase authentication
     */
    public void guestLogin(View v){

        fAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Intent i = new Intent(LoginActivity.this, DashboardMeteActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}