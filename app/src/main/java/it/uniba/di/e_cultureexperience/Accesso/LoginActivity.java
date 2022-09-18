package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.charset.Charset;
import java.util.Random;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.R;

public class LoginActivity extends AppCompatActivity {

    private static EditText txtEmailLogin, txtPasswordLogin;
    private FirebaseAuth fAuth;
    private ToggleButton visibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmailLogin = findViewById(R.id.emailLogin);
        txtPasswordLogin = findViewById(R.id.passwordLogin);
        fAuth = FirebaseAuth.getInstance();
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView txtGoToRegistration = findViewById(R.id.registrationTxt);
        CheckBox rememberBox = findViewById(R.id.checkBox);
        visibility = findViewById(R.id.visibilityToggleButton);



        loginBtn.setOnClickListener(v -> loginBtnClick());

        txtGoToRegistration.setOnClickListener(v -> goToRegistration());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");

        if(checkbox.equals("true")){
            Intent intent = new Intent(LoginActivity.this, DashboardMeteActivity.class);

            startActivity(intent);
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

    }

    public void loginBtnClick(){
        String email = txtEmailLogin.getText().toString().trim();
        String password = txtPasswordLogin.getText().toString().trim();
        //Controllo email vuota
        if(email.isEmpty()){
            txtEmailLogin.setError("Email richiesta!");
            txtEmailLogin.requestFocus();
            return;
        }
        //Controllo password vuota
        if(password.isEmpty()){
            txtPasswordLogin.setError("Password richiesta!");
            txtPasswordLogin.requestFocus();
            return;
        }
        (fAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                Toast.makeText(LoginActivity.this, "Login effettuato", Toast.LENGTH_LONG).show();

                Intent i = new Intent(LoginActivity.this, DashboardMeteActivity.class);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void forgotTxtClick(View v){
        Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(i);
    }

    public void goToRegistration(){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Permette di visualizzare la password oppure di nasconderla
     */
    public void onVisibiblityToggleButton(View view) {
        int PASSWORD_INPUT_TYPE;
        if(visibility.isChecked())
            PASSWORD_INPUT_TYPE = 129;
        else
            PASSWORD_INPUT_TYPE = 1;

        txtPasswordLogin.setInputType(PASSWORD_INPUT_TYPE);
    }
}