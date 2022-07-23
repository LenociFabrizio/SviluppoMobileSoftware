package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import it.uniba.di.e_cultureexperience.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText txtEmailReset;
    private Button resetPasswordBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txtEmailReset = findViewById(R.id.emailReset);
        resetPasswordBtn = findViewById(R.id.resetBtn);
        fAuth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword(){

        String email = txtEmailReset.getText().toString().trim();

        if(email.isEmpty()){
            txtEmailReset.setError("Email richiesta!");
            txtEmailReset.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmailReset.setError("Controlla email inserita!");
            txtEmailReset.requestFocus();
            return;
        }

        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgotPasswordActivity.this, "Controlla la tua email!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}