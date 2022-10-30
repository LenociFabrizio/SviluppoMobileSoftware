package it.uniba.di.e_cultureexperience.QRScanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import it.uniba.di.e_cultureexperience.OggettoDiInteresse.MostraOggettoDiInteresseActivity;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private final int CAMERA_PERMISSION_CODE = 1;

    @Override
    public void onCreate(Bundle state) {
        //controllo i permessi
        String permesso = Manifest.permission.CAMERA;
        checkCameraPermission(permesso,CAMERA_PERMISSION_CODE);

        super.onCreate(state);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        // Set the scanner view as the content view
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Risultato Scansione");
        String contentScanned = rawResult.getText();

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
                                        Intent intent = new Intent(getApplicationContext(), MostraOggettoDiInteresseActivity.class);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

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

        // Do something with the result here
        // Prints scan results
    }

    public void checkCameraPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Showing the toast message
                //Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}