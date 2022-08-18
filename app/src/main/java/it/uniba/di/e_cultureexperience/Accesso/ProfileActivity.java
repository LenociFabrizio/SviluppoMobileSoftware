package it.uniba.di.e_cultureexperience.Accesso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.R;
import it.uniba.di.e_cultureexperience.QuizGame.StartGame;

public class ProfileActivity extends AppCompatActivity {

    private TextView nickname;
    private final int TAKE_IMAGE_CODE = 10001;
    private int CAMERA_PERMISSION_CODE = 1;

    private FirebaseAuth fAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView profileImageView;
    String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Locale locale = new Locale("it-rIT");
        Locale.setDefault(locale);

        TextView email = findViewById(R.id.emailView);
        fAuth = FirebaseAuth.getInstance();
        profileImageView = findViewById(R.id.profileImage);
        nickname = findViewById(R.id.nicknameView);

        /*Button language_button = findViewById(R.id.languageBtn);

        language_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en-rGB");
                }
        });

        Button language_button_ita = findViewById(R.id.languageBtnIta);

        language_button_ita.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {
                                                       setLocale("it-rIT");
                                                   }
                                               }
        );*/

        //foto profilo
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl()).into(profileImageView);
            }
        }

        //Faccio visualizzare l'email, password con cui ha effettuato l'accesso
        email.setText(fAuth.getCurrentUser().getEmail());

        //Ricercare il proprio nickname con idDB == idLocale
        db.collection("utenti")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String nicknameDataBase = document.getString("nickname");
                                String idDataBase = document.getString("idUtente");
                                Log.d("FETCH", document.getId() + " leggo=> " + nicknameDataBase);

                                if (idDataBase.equals(fAuth.getUid())) {
                                    Log.d("ID UTENTE UGUALE", idDataBase + " = " + fAuth.getUid());
                                    nickname.setText(nicknameDataBase);
                                    return;

                                } else {
                                    Log.d("ID NON TROVATO", "ERROR");
                                }
                            }
                        } else {
                            Log.d("DB VUOTO", "ERROR");
                        }
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }

                });
        onCreateBottomNavigation();
    }

    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_profile);

        boolean loginGoogle = getIntent().getExtras().getBoolean("segnalino");

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:

                        //entro nell'altra activity immettendo il segnalino appena caricato
                        startActivity(new Intent(getApplicationContext(), DashboardMete.class).putExtra("segnalino",loginGoogle));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_scan:

                        //entro nell'altra activity immettendo il segnalino appena caricato
                        startActivity(new Intent(getApplicationContext(), FirstAccessActivity.class).putExtra("segnalino",loginGoogle));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class).putExtra("segnalino",loginGoogle));
                        overridePendingTransition(0,0);
                        return true;

                }

                /*if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }*/
                return false;
            }
        });
    }

    //TEST
    public void launchQuizGame(View view){
        Intent i = new Intent(ProfileActivity.this, StartGame.class);
        startActivity(i);
    }

    public void changePassw(View v){
        Intent i = new Intent(ProfileActivity.this, ForgotPasswordActivity.class);
        startActivity(i);
    }

    public void signOut(View v){
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();

        fAuth.signOut();
        Intent i = new Intent(ProfileActivity.this, FirstAccessActivity.class);
        startActivity(i);
        finish();
    }


    public void handleImageClick(View view) {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ProfileActivity.this, "Cheese!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, TAKE_IMAGE_CODE);
            }
        } else {
            requestStoragePermission();
        }

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Concedi permesso")
                    .setMessage("Questo permesso serve per poter accedere alla fotocamera per impostare la propria foto profilo.")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE))
                    .setNegativeButton("cancella", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permesso Concesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permesso Rifiutato", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_IMAGE_CODE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profileImageView.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser(). getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("profileImages").child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> getDownloadUrl(reference)).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e.getCause()));
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "onSuccess: " + uri);
            setUserProfileUrl(uri);
        });
    }

    private void setUserProfileUrl(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ProfileActivity.this,"Updated succesfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, ProfileActivity.class);
        startActivity(refresh);
        finish();
    }
}