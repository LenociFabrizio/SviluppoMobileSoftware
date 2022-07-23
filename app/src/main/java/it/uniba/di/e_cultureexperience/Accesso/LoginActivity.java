package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.R;

public class LoginActivity extends AppCompatActivity {
    private static EditText txtEmailLogin, txtPasswordLogin;
    private FirebaseAuth fAuth;
    private TextView txtGoToRegistration;
    private Button loginBtn;
    private CheckBox rememberBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmailLogin = findViewById(R.id.emailLogin);
        txtPasswordLogin = findViewById(R.id.passwordLogin);
        fAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        txtGoToRegistration = findViewById(R.id.registrationTxt);
        rememberBox = findViewById(R.id.checkBox);

        loginBtn.setOnClickListener(v -> loginBtnClick());

        txtGoToRegistration.setOnClickListener(v -> goToRegistration());

        //todo: se clicco la checkbox senza inserire credenziali durante il primo accesso e riapro l'app, mi fa accedere ugualmente. Guarda riga 54
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");

        if(checkbox.equals("true")){
            Intent intent = new Intent(LoginActivity.this, DashboardMete.class);
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
    //todo: mettere *** quando digiti la password
    public void loginBtnClick(){
        String email = txtEmailLogin.getText().toString().trim();
        String password = txtPasswordLogin.getText().toString().trim();
        //Controllo email vuota
        if(email.isEmpty()){
            txtEmailLogin.setError("Email richiesta!");;
            txtEmailLogin.requestFocus();
            return;
        }
        //Controllo password vuota
        if(password.isEmpty()){
            txtPasswordLogin.setError("Password richiesta!");;
            txtPasswordLogin.requestFocus();
            return;
        }
        (fAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(LoginActivity.this, /*QUI*/DashboardMete.class);
                    startActivity(i);
                    finish();
                }else{
                    Log.e("ERROR ---> ", task.getException().toString());
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
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


}