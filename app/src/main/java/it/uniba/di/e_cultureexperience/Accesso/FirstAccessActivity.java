package it.uniba.di.e_cultureexperience.Accesso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import it.uniba.di.e_cultureexperience.R;

public class FirstAccessActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    boolean loginGoogle=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_access);

        final SignInButton loginButton = findViewById(R.id.sign_in_button);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc=GoogleSignIn.getClient(this,gso);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

    }

    private void SignIn(){

        Intent intent=gsc.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                task.getResult(ApiException.class);
                loginGoogle=true;
                entraInGoogleActivity();
            }catch(ApiException ex){
                Toast.makeText(getApplicationContext(),"qualcosa è andato storto",Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }

        }
    }

    private void entraInGoogleActivity(){
        finish();
        Intent intent=new Intent(getApplicationContext(),LoginGoogleActivity.class);
        //mi porto nell'activity DashbordMete il segnalino
        intent.putExtra("segnalino",loginGoogle);
        startActivity(intent);
    }
    private void handleSignInTask(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // Google Sign In was successful, authenticate with Firebase
            startActivity(new Intent(FirstAccessActivity.this, ProfileActivity.class));
            finish();
        } catch (ApiException e) {
            e.printStackTrace();
            // Google Sign In failed, update UI appropriately
        }
    }

    public void registerBtnClick(View v){
        Intent i = new Intent(FirstAccessActivity.this, RegistrationActivity.class);
        startActivity(i);
        finish();
    }

    public void loginBtnClick(View v){
        Intent i = new Intent(FirstAccessActivity.this, LoginActivity.class);
        //mi porto nell'activity LoginActivity il segnalino
        i.putExtra("segnalino",loginGoogle);
        startActivity(i);
        finish();
    }
}