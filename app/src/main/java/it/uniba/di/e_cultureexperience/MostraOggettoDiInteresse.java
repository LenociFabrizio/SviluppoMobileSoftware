package it.uniba.di.e_cultureexperience;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MostraOggettoDiInteresse extends Activity {

    private TextView nomeOggetto, descrizioneOggetto, bluetoothOggetto;
    private ImageView immagineOggetto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oggetto_di_interesse);

        nomeOggetto = findViewById(R.id.nomeTxt);
        descrizioneOggetto = findViewById(R.id.descrizioneTxt);
        immagineOggetto = findViewById(R.id.immagineOggetto);
        bluetoothOggetto  = findViewById(R.id.bluetoothIdTxt);

        //Prendo l'oggetto passato dall'intent
        OggettoDiInteresse oggetto = getIntent().getExtras().getParcelable("oggettoDiInteresse");
        Log.d("OggettoDiInteresse => ", oggetto.toString());

        //S T A R T - set content into layout
        Picasso.with(this)
                .load(oggetto.getUrl_immagine())
                .into(immagineOggetto);

        nomeOggetto.setText(oggetto.getNome());

        descrizioneOggetto.setText(oggetto.getDescrizione());

        bluetoothOggetto.setText(oggetto.getBluetooth_id());
        //F I N I S H

    }
}