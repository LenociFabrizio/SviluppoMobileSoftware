package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import it.uniba.di.e_cultureexperience.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialButton resetPasswordBtn;

    private TextInputEditText editTextEmail;
    private TextInputLayout layoutEmail;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetPasswordBtn = findViewById(R.id.resetBtn);

        editTextEmail = findViewById(R.id.emailInputEditText);
        layoutEmail = findViewById(R.id.emailTextInputLayout);

        fAuth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword(){

        String email = editTextEmail.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()){
            //txtEmailReset.setError("Email richiesta!");
            //txtEmailReset.requestFocus();
            layoutEmail.setError("Inserisci correttamente email d'accesso");
            layoutEmail.requestFocus();
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