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
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.R;

public class LoginActivity extends AppCompatActivity {
    private static EditText txtEmailLogin, txtPasswordLogin;
    private FirebaseAuth fAuth;

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

        loginBtn.setOnClickListener(v -> loginBtnClick());

        txtGoToRegistration.setOnClickListener(v -> goToRegistration());

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");

        if(checkbox.equals("true")){
            Intent intent = new Intent(LoginActivity.this, DashboardMete.class);

            //mi carico il segnalino dal first access activity
            boolean loginGoogle = getIntent().getExtras().getBoolean("segnalino");
            //immetto segnalino in dashboard mete
            intent.putExtra("segnalino",loginGoogle);

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
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();

                Intent i = new Intent(LoginActivity.this, /*QUI*/DashboardMete.class);

                //mi carico il segnalino dal first activity
                boolean loginGoogle = getIntent().getExtras().getBoolean("segnalino");
                //immetto segnalino in dashboard mete
                i.putExtra("segnalino",loginGoogle);

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


}