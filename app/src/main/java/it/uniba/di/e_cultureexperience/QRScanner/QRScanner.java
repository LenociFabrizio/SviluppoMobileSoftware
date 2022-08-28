package it.uniba.di.e_cultureexperience.QRScanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import it.uniba.di.e_cultureexperience.Accesso.LoginActivity;
import it.uniba.di.e_cultureexperience.DashboardMete;
import it.uniba.di.e_cultureexperience.R;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScanner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("For flash use volume up key");
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
            builder.setTitle("result");
            builder.setMessage( result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //mi porta dov ero
                    Intent intent = new Intent(QRScanner.this, /*QUI*/DashboardMete.class);

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

                    startActivity(intent);
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }else{
            Toast.makeText(getApplicationContext(), "You didn' t scan anything", Toast.LENGTH_SHORT).show();
        }

    }
}
