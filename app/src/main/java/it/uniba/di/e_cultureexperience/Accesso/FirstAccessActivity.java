package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.R;

public class FirstAccessActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_access);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                task.getResult(ApiException.class);
            }catch(ApiException ex){
                Toast.makeText(getApplicationContext(),"qualcosa è andato storto",Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }

        }
    }

    public void registerBtnClick(View v){
        Intent i = new Intent(FirstAccessActivity.this, RegistrationActivity.class);
        startActivity(i);
        finish();
    }

    public void loginBtnClick(View v){
        Intent i = new Intent(FirstAccessActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void guestLogin(View v){

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d("GUEST", "signInAnonymously:success");
                        Intent i = new Intent(FirstAccessActivity.this, DashboardMeteActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("GUEST", "signInAnonymously:failure", task.getException());
                        Toast.makeText(FirstAccessActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });

    }
}