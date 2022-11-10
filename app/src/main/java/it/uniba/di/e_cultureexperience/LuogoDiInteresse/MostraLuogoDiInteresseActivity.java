package it.uniba.di.e_cultureexperience.LuogoDiInteresse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.Percorso.MostraPercorsoActivity;
import it.uniba.di.e_cultureexperience.Percorso.PercorsiAdapter;
import it.uniba.di.e_cultureexperience.Percorso.Percorso;
import it.uniba.di.e_cultureexperience.QrCodeScanner;
import it.uniba.di.e_cultureexperience.R;

public class MostraLuogoDiInteresseActivity extends AppCompatActivity {
    private ArrayList<Percorso> percorsi;
    ListView list_view_percorsi;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    final String collectionPath = "luoghiPreferiti";

    private ToggleButton favorite;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.luogo_di_interesse);
        //prendo l' oggetto passato dall' intent
        LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");

        percorsi = new ArrayList<>();
        list_view_percorsi = findViewById(R.id.lista_percorsi);

        Button importPercorsoBtn = findViewById(R.id.importBtn);

        importPercorsoBtn.setOnClickListener(v ->
                {
                    //Log.d("Lettura_percorso", getPercorsoByJson().toString());
                    //Log.d("Lettura_oggetti", getOggettiDiInteresseByJson().toString());

                    Percorso percorsoImportato = new Percorso(getPercorsoByJson().getNome(), getPercorsoByJson().getDescrizione()
                            , getPercorsoByJson().getDurata(), getPercorsoByJson().getMeta(), getPercorsoByJson().getId());
                    //Se il percorso importato fa parte dello stesso luogo, allora l'aggiungo alla listViewPercorsi
                    if(percorsoImportato.getMeta().equals(luogo.getNome())){
                        ArrayList<OggettoDiInteresse> oggettiImportati = new ArrayList<>(getOggettiDiInteresseByJson());

                        Context context = getApplicationContext();
                        Intent i = new Intent(context, MostraPercorsoActivity.class);
                        Bundle args = new Bundle();

                        args.putSerializable("ARRAYLIST", oggettiImportati);
                        i.putExtra("BUNDLE",args);

                        i.putExtra("percorso", percorsoImportato);

                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(i);
                    }else{
                        Toast.makeText(this, "Percorso selezionato non appartiene alla meta desiderata, cambiala.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ImageView immagine = findViewById(R.id.immagine);
        TextView descrizioneLuogo = findViewById(R.id.descrizione);
        favorite = findViewById(R.id.favoriteToggleButton);

        isLuogoPreferito(luogo);

        setLuogoDiInteresse(immagine, descrizioneLuogo, luogo);

        Toolbar mToolbar = findViewById(R.id.toolbar_luogodiinteresse);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(luogo.getNome());

        CollapsingToolbarLayout collapsingLayout = findViewById(R.id.collapsing_toolbar);
        Log.e("colore",getResources().getString(R.color.black));
        collapsingLayout.setExpandedTitleColor(Color.parseColor(getResources().getString(R.color.white)));
        collapsingLayout.setCollapsedTitleTextColor(Color.parseColor(getResources().getString(R.color.black)));

        onCreateBottomNavigation();

    }

    private void setLuogoDiInteresse(ImageView immagine, TextView descrizioneLuogo, LuogoDiInteresse luogo) {
        Picasso.with(this)
                .load(luogo.getUrl_immagine())
                .into(immagine);
        descrizioneLuogo.setText(luogo.getDescrizione());
        setPercorsi(luogo);
    }

    /**
     * Prelevo da un file json le informazioni del percorso: nome, descrizione, durata, meta, id
     */
    private Percorso getPercorsoByJson() {
        String json;
        try {
            InputStream is = getAssets().open("pi.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);

            return new Percorso(obj.getString("nome"), obj.getString("descrizione")
                    , obj.getInt("durata"), obj.getString("meta"), obj.getString("id"));

        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
            return null;
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }

    }

    /**
     * Prelevo da un file json le informazioni degli oggetti di interesse restituendoli in un arrayList di tipo oggettoDiInteresse. Se il percorso non ha oggettiDiInteresse,
     */
    private ArrayList<OggettoDiInteresse> getOggettiDiInteresseByJson() {

        ArrayList<OggettoDiInteresse> arrayList = new ArrayList<>();
        String json;

        try {
            InputStream is = getAssets().open("pi.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);

            JSONArray oggettiDiInteresseJsonArray = obj.getJSONArray("oggettiDiInteresse");

            if(oggettiDiInteresseJsonArray != null) {

                for (int i = 0; i < oggettiDiInteresseJsonArray.length(); i++) {
                    JSONObject oggettoDiInteresseJson = oggettiDiInteresseJsonArray.getJSONObject(i);
                    //Creo oggetto
                    OggettoDiInteresse oggettoDiInteresse = new OggettoDiInteresse(oggettoDiInteresseJson.getString("id"), oggettoDiInteresseJson.getString("nome"),
                            oggettoDiInteresseJson.getString("descrizione"), oggettoDiInteresseJson.getString("url_immagine"), oggettoDiInteresseJson.getString("bluetooth_id"),
                            oggettoDiInteresseJson.getDouble("latitudine"), oggettoDiInteresseJson.getDouble("longitudine"));
                    //Aggiungo oggetto appena creato nell'array
                    arrayList.add(oggettoDiInteresse);
                }

                return arrayList;

            }else{
                Toast.makeText(this, "Il percorso non ha oggetti di interesse da visitare, riprova con un altro percorso!", Toast.LENGTH_SHORT).show();
                return null;
            }

        }catch (IOException | JSONException e){
            e.printStackTrace();
            return null;
        }

    }



    @SuppressLint("NonConstantResourceId")
    public void onCreateBottomNavigation(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Set Home Selected
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), DashboardMeteActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.nav_scan:
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.secondary_top_menu, menu);

        LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        final int sizeDataBase = task.getResult().size();
                        if (sizeDataBase != 0) {
                            boolean luogoPreferitoEsistente = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idUtenteDatabase = document.getString("idUtente");
                                String nomeLuogoDatabase = document.getString("nome");
                                //Posso aggiungere il luogoScelto solo se non è stato aggiunto precedentemente
                                if(idUtenteDatabase.equals(fAuth.getUid()) && nomeLuogoDatabase.equals(luogo.getNome())){

                                    luogoPreferitoEsistente = true;


                                }
                            }//fine for

                        }
                    }
                });

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:
                LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");

                Intent intent =new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Vieni a vedere "+luogo.getNome()+"\n\n"+luogo.getDescrizione()+"\n\n"+"Scaricati l'app ECulture-Experience!");
                startActivity(Intent.createChooser(intent,"Condividi il luogo di interesse"));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void setPercorsi(LuogoDiInteresse luogo){

        //prendo i percorsi di quella meta
        db.collection("percorsi")
                .whereEqualTo("meta", luogo.getId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Percorso temp = document.toObject(Percorso.class);
                            temp.setId(document.getId());
                            percorsi.add(temp);
                        }
                        PercorsiAdapter customAdapter = new PercorsiAdapter(getApplicationContext(), percorsi);
                        list_view_percorsi.setAdapter(customAdapter);
                    } else {
                        Log.w("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });
    }

    /**
     * Controllo se il luogo è già nei preferiti. Nel caso lo fosse, rendo il favoriteBtn colorato di rosso
     */
    public void isLuogoPreferito(LuogoDiInteresse luogo){
        final int[] value = new int[1];
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        final int sizeDataBase = task.getResult().size();
                        if (sizeDataBase != 0) {
                            boolean luogoPreferitoEsistente = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idUtenteDatabase = document.getString("idUtente");
                                String nomeLuogoDatabase = document.getString("nome");
                                //Posso aggiungere il luogoScelto solo se non è stato aggiunto precedentemente
                                if(idUtenteDatabase.equals(fAuth.getUid())
                                        && nomeLuogoDatabase.equals(luogo.getNome())){
                                    favorite.setChecked(true);
                                    Toast.makeText(this, "Meta aggiunta all'elenco dei preferiti",Toast.LENGTH_SHORT).show();
                                    luogoPreferitoEsistente = true;
                                    value[0] = 2;

                                }
                            }//fine for
                            if (!luogoPreferitoEsistente){
                                favorite.setChecked(false);
                            }
                        }
                    }
                });
    }

    /**
     * Al click del favoriteButton richiama la funzione per inserire il determinato luogo di interesse come preferito o rimuoverlo
     */
    public void onFavoriteToggleClick(View view) {

        //prendo l' oggetto passato dall' intent
        LuogoDiInteresse luogo = getIntent().getExtras().getParcelable("luogoDiInteresse");

        /**
         * se favoriteBtn ha il checked TRUE(sta già nei preferiti) allora se lo clicco elimino, altrimenti il contrario
         */
        if(favorite.isChecked()){
            db.collection(collectionPath)
                    .get()
                    .addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {

                            int sizeDataBase = task1.getResult().size();
                            if(sizeDataBase == 0){
                                scritturaLuogoDatabase(luogo, fAuth.getUid());
                                return;
                            }

                            scritturaLuogoDatabase(luogo, fAuth.getUid());
                        }else{
                            Log.e("Error", "Errore server metaPreferita.");
                        }
                    });
        }else{
            eliminazioneLuogoDatabase(luogo, fAuth.getUid());
        }

    }

    /**
     * Scrittura su database metaPreferita del luogoScelto
     */
    public void scritturaLuogoDatabase(LuogoDiInteresse luogo, String idUtente) {
        Map<String, String> luogoScelto = new HashMap<>();
        luogoScelto.put("nome", luogo.getNome());
        luogoScelto.put("descrizione", luogo.getDescrizione());
        luogoScelto.put("idUtente", idUtente);
        luogoScelto.put("url_immagine", luogo.getUrl_immagine());

        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        final int sizeDataBase = task.getResult().size();
                        boolean luogoDuplicato = false;
                        int singolaRigaDatabase = 0;
                        if (sizeDataBase != 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                singolaRigaDatabase++;

                                String idUtenteDatabase = document.getString("idUtente");
                                String nomeLuogoDatabase = document.getString("nome");
                                //Posso aggiungere il luogoScelto solo se non è stato aggiunto precedentemente
                                if(idUtenteDatabase.equals(fAuth.getUid()) && nomeLuogoDatabase.equals(luogoScelto.get("nome"))){
                                    luogoDuplicato = true;
                                }

                                if (!luogoDuplicato && singolaRigaDatabase == sizeDataBase){
                                    addDoc(collectionPath, luogoScelto);
                                }
                            }//fine for

                        }else{
                            addDoc(collectionPath, luogoScelto);
                        }
                        Toast.makeText(this, R.string.meta_added,Toast.LENGTH_SHORT).show();

                    }
                });
    }

    /**
     * Permette di aggiungere un documento su firestore database
     */
    public void addDoc(String collectionPath, Map<String, String> luogoScelto){
        db.collection(collectionPath)
                .add(luogoScelto)
                .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
    }

    public void eliminazioneLuogoDatabase(LuogoDiInteresse luogo, String idUtente){
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        final int sizeDataBase = task.getResult().size();
                        if (sizeDataBase != 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String idUtenteDatabase = document.getString("idUtente");
                                String nomeLuogoDatabase = document.getString("nome");
                                //Faccio coincidere l'id dell'utente loggato con quello del database
                                if(idUtenteDatabase.equals(idUtente) && nomeLuogoDatabase.equals(luogo.getNome())){
                                    deleteDoc(collectionPath, document.getId());
                                }
                            }
                        }
                        Toast.makeText(this, R.string.meta_removed,Toast.LENGTH_SHORT).show();

                    }else{
                        Log.e("Error", "Errore nella lettura del database metePreferite!");
                    }
                });
    }

    public void deleteDoc(String collectionPath, String documentReference)
    {
        db.collection(collectionPath).document(documentReference)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("Delete", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e ->
                        Log.w("Delete", "Error deleting document", e));
    }
}