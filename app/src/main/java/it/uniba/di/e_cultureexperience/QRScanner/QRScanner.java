package it.uniba.di.e_cultureexperience.QRScanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.uniba.di.e_cultureexperience.OggettoDiInteresse.MostraOggettoDiInteresseActivity;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import it.uniba.di.e_cultureexperience.R;

public class QRScanner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Stringa per il flash");
        integrator.setOrientationLocked(true);
        integrator.setCaptureActivity(Capture.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result.getContents() != null )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Risultato Scansione");
            String contentScanned = result.getContents();

            //controllo se corrisponde al QR di un oggetto di interesse
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = rootRef.collection("oggetti").document(String.valueOf(contentScanned));
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //CORRISPONDE al QR di un oggetto
                            //CHIEDO ALL' UTENTE SE VUOLE VISUALIZZARLO
                            builder.setMessage("Vuoi visualizzare l' oggetto scansionato?");
                            builder.setPositiveButton("Conferma",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            OggettoDiInteresse oggettoDiInteresse = document.toObject(OggettoDiInteresse.class);
                                            oggettoDiInteresse.setId(document.getId());
                                            Intent intent = new Intent(QRScanner.this, MostraOggettoDiInteresseActivity.class);
                                            intent.putExtra("oggettoDiInteresse", oggettoDiInteresse);
                                            intent.putExtra("scannerizzato", true);
                                            startActivity(intent);
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    //rimane alla scannerizzazione
                                    //Toast.makeText(getApplicationContext(), "You didn' t scan anything", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            //NON CORRISPONDE al QR di un oggetto di interesse
                            AlertDialog.Builder builder = new AlertDialog.Builder(QRScanner.this);

                            builder.setMessage("Il QR Code scansionato  non corrisponde ad un Oggetto di Interesse");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            });
                            builder.show();
                        }
                    } else {
                        Log.d("Endrit", "Failed with: ", task.getException());
                    }
                }
            });
                //prendo l id preso
                //se il percorso è avviato, controllo se corrisponde all' oggetto che deve trovare
                    //se non corrisponde all' oggetto da trovare ritorna all' activity del percorso
                    //se corrisponde all' oggetto da trovare lo mostra
                //se il percorso non è avviato
                    //mostra l' oggetto scannerizzato
                //mi carico il segnalino dal first activity
                //boolean loginGoogle = getIntent().getExtras().getBoolean("segnalino");
                //immetto segnalino in dashboard mete
                //i.putExtra("segnalino",loginGoogle);
        }else{
            Toast.makeText(getApplicationContext(), "You didn' t scan anything", Toast.LENGTH_SHORT).show();
        }

    }
}
