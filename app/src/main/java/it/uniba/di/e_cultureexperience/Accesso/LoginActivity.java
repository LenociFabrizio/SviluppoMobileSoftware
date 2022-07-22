package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtEmailLogin = findViewById(R.id.emailLogin);
        txtPasswordLogin = findViewById(R.id.passwordLogin);
        fAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        txtGoToRegistration = findViewById(R.id.registrationTxt);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtnClick();
            }
        });

        txtGoToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistration();
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

    public static String getTxtEmailLogin() {
        return txtEmailLogin.getText().toString().trim();
    }

    public static String getTxtPasswordLogin() {
        return txtPasswordLogin.getText().toString().trim();
    }

    public void goToRegistration(){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }


}