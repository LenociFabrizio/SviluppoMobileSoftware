package it.uniba.di.e_cultureexperience.Accesso;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.LuogoDiInteresse.MostraLuogoDiInteressePreferitoActivity;
import it.uniba.di.e_cultureexperience.QrCodeScanner;
import it.uniba.di.e_cultureexperience.R;

public class ProfileActivity extends AppCompatActivity implements ListItemAdapter.ItemClickListener{

    private TextView nickname;
    private final int TAKE_IMAGE_CODE = 10001;
    private final int CAMERA_PERMISSION_CODE = 1;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView profileImageView;

    private final List<String> menuArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Locale current = getResources().getConfiguration().locale;

        Log.e("lingua", current.getLanguage());

        TextView email = findViewById(R.id.emailView);
        profileImageView = findViewById(R.id.profileImage);
        nickname = findViewById(R.id.nicknameView);

        RecyclerView listaMenu = findViewById(R.id.lista_menu);
        listaMenu.setLayoutManager(new LinearLayoutManager(this));

        ListItemAdapter adapter = new ListItemAdapter(menuArray,this);
        adapter.setClickListener(this);
        listaMenu.setAdapter(adapter);


        menuArray.add(getString(R.string.favourite_destination));
        menuArray.add(getString(R.string.edit_profile));
        menuArray.add(getString(R.string.app_preferences));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_divider));
        listaMenu.addItemDecoration(dividerItemDecoration);
        listaMenu.setAdapter(adapter);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if(user.isAnonymous()){
                //todo: aggiungere in @string
                email.setText("Sei ospite!");
                nickname.setText("@ECE" + fAuth.getUid().substring(1, 4));
            }else{
                if (user.getPhotoUrl() != null) {
                    Glide.with(this).load(user.getPhotoUrl()).into(profileImageView);
                }
                email.setText(fAuth.getCurrentUser().getEmail());
                setNickname();
            }

        }

        onCreateBottomNavigation();
    }

    /**
     * Ricerca il proprio nickname nel database collectinPath e lo mostro in output
     */
    public void setNickname(){

        String collectionPath = "utenti";
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String nicknameDataBase = document.getString("nickname");
                            String idDataBase = document.getString("idUtente");

                            if (idDataBase.equals(fAuth.getUid())) {
                                nickname.setText("@" + nicknameDataBase);
                                return;

                            } else {
                                Log.d("Error", "ID non trovato");
                            }
                        }
                    } else {
                        Log.w("Error", "Error getting documents.", task.getException());
                    }
                });
    }


    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.nav_home:
                    //entro nell'altra activity immettendo il segnalino appena caricato
                    startActivity(new Intent(getApplicationContext(), DashboardMeteActivity.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.nav_scan:
                    //entro nell'altra activity immettendo il segnalino appena caricato
                    startActivity(new Intent(getApplicationContext(), QrCodeScanner.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.nav_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    public void signOut(View v){
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();

        fAuth.signOut();
        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }


    public void handleImageClick(View view) {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(ProfileActivity.this, "Cheese!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, TAKE_IMAGE_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permesso Concesso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            } else {
                Toast.makeText(this, "Permesso Rifiutato", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_IMAGE_CODE){
            if (resultCode == RESULT_OK) {
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

        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> getDownloadUrl(reference)).addOnFailureListener(e -> Log.e("TAG", "onFailure: ", e.getCause()));
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("TAG", "onSuccess: " + uri);
            setUserProfileUrl(uri);
        });
    }

    private void setUserProfileUrl(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

        user.updateProfile(request)
                .addOnSuccessListener(unused -> Toast.makeText(ProfileActivity.this,"Updated succesfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Profile image failed...", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(View view, int position) {
        if(position == 0)
            startActivity(new Intent(ProfileActivity.this, MostraLuogoDiInteressePreferitoActivity.class));
        if(position == 1)
            startActivity(new Intent(ProfileActivity.this, ForgotPasswordActivity.class));
        if(position == 2)
            startActivity(new Intent(ProfileActivity.this, CambiaLinguaActivity.class));
    }
}