package it.uniba.di.e_cultureexperience.Percorso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettiDiInteresseAvviaPercorsoAdapter;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.R;

public class AvviaPercorsoActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<OggettoDiInteresse> oggettoDiInteresseArrayList = new ArrayList<>(), oggettoDiInteresseArrayListLocale = new ArrayList<>();
    private ListView listViewOggetti;
    private ArrayList<String> idOggetti = new ArrayList<>();
    private int i = 0;
    private final String collectionPath = "oggetti";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avvia_percorso);

        listViewOggetti = findViewById(R.id.listaOggettiListView);

        Percorso percorso = getIntent().getExtras().getParcelable("percorso");

        idOggetti.addAll(retrieveIdOggettiFromPercorso(percorso));
        //Visualizzo oggetti in Output
        letturaOggetti();

        db.collection(collectionPath)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idOggetto = document.getId();
                            if(idOggetti.contains(idOggetto))
                            {
                                OggettoDiInteresse oggettoDiInteresse = document.toObject(OggettoDiInteresse.class);
                                oggettoDiInteresse.setId(document.getId());
                                //Qui dentro ci sono gli oggetti del percorso scelto precedentemente
                                oggettoDiInteresseArrayListLocale.add(oggettoDiInteresse);

                                ArrayList<OggettoDiInteresse> oggettiNonVisitatiList;
                                oggettiNonVisitatiList = (resetVisitato(oggettoDiInteresseArrayListLocale));
                                Log.d("Size ArrayList locale", " -> " + oggettiNonVisitatiList.size());

                            }
                        }
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });

        /*if(oggettoDiInteresseArrayList.size() != 0){
            Log.d("Contenuto ArrayList", oggettoDiInteresseArrayList.get(1).getNome());
        }else{
            Log.d("Contenuto ArrayList", "Vuoto " + oggettoDiInteresseArrayList.size());
        }*/

        //Non c'è nulla dentro questo array
        //java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        /*oggettoDiInteresseArrayList = resetVisitato(oggettoDiInteresseArrayList);

        if(!(oggettoDiInteresseArrayList.get(i).isVisitato())){
            Intent intent = new Intent(AvviaPercorsoActivity.this, OggettiDiInteresseAvviaPercorsoAdapter.class);
            intent.putExtra("idOggetto", oggettoDiInteresseArrayList.get(i).getId());
        }else{
            i++;
        }*/



    }

    /**
     * Prelevo gli id degli oggetti che sono contenuti nel percorso
     * @param percorso
     * @return ArrayList di idOggetti
     */
    public ArrayList<String> retrieveIdOggettiFromPercorso(Percorso percorso){

        db.collection("/percorsi/"+percorso.getId()+"/oggetti")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idOggetto = document.getId();
                            idOggetti.add(idOggetto);
                        }
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });

        return idOggetti;
    }

    /**
     * Con gli idDegli oggetti del percorso, prelevo tutte le informazioni degli oggetti e li mostro in Output chiamando l'adapter
     * return oggettoDiInteresseList
     */
    public void letturaOggetti(){

        db.collection(collectionPath)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idOggetto = document.getId();
                            if(idOggetti.contains(idOggetto))
                            {
                                OggettoDiInteresse oggettoDiInteresse = document.toObject(OggettoDiInteresse.class);
                                oggettoDiInteresse.setId(document.getId());
                                oggettoDiInteresseArrayList.add(oggettoDiInteresse);
                            }
                        }
                        OggettiDiInteresseAvviaPercorsoAdapter customAdapter = new OggettiDiInteresseAvviaPercorsoAdapter(getApplicationContext(), oggettoDiInteresseArrayList);
                        listViewOggetti.setAdapter(customAdapter);
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });
    }


    /**
     * Cambio visitato in true se l'id scannerizzato è uguale a quello dell'oggetto da visitare
     * @param idScanned
     * @param oggettoDiInteresseList
     * @param posizione
     */
    public void checkIdScanned(String idScanned, ArrayList<OggettoDiInteresse> oggettoDiInteresseList, int posizione){

        if(idScanned.equals(oggettoDiInteresseList.get(posizione).getId())){
            OggettoDiInteresse oggetto = new OggettoDiInteresse(
                    oggettoDiInteresseList.get(posizione).getId(),
                    oggettoDiInteresseList.get(posizione).getNome(),
                    oggettoDiInteresseList.get(posizione).getDescrizione(),
                    oggettoDiInteresseList.get(posizione).getUrl_immagine(),
                    oggettoDiInteresseList.get(posizione).getBluetooth_id(),
                    true);

            oggettoDiInteresseList.add(oggetto);
        }
    }
    /**
     * Permette di resettare la variabile booleana visitato a false di tutti gli oggetti contenuti nell'ArrayList
     * @param oggettoDiInteresseList
     * @return oggettoDiInteresseList
     */
    public ArrayList<OggettoDiInteresse> resetVisitato(ArrayList<OggettoDiInteresse> oggettoDiInteresseList){

        for(int i = 0; i < oggettoDiInteresseList.size(); i++){
            OggettoDiInteresse oggetto = new OggettoDiInteresse(
                    oggettoDiInteresseList.get(i).getId(),
                    oggettoDiInteresseList.get(i).getNome(),
                    oggettoDiInteresseList.get(i).getDescrizione(),
                    oggettoDiInteresseList.get(i).getUrl_immagine(),
                    oggettoDiInteresseList.get(i).getBluetooth_id(),
                    false);

            oggettoDiInteresseList.add(oggetto);
        }

        return oggettoDiInteresseList;
    }


}