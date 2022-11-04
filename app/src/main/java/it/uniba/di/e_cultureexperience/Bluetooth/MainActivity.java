package it.uniba.di.e_cultureexperience.Bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import it.uniba.di.e_cultureexperience.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//todo: cambiare nome di questa activity
public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Use this check to determine whether Bluetooth classic is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) || bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non supportato", Toast.LENGTH_SHORT).show();
            finish();
        }
        //ALLORA BT è disponibile, devo controllare se è attivo
        if (!bluetoothAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                requestBluetoothConnectPermission();
            }
        } else {
            //è abilitato e continuo con l' avvio del servizio
            Intent serviceIntent = new Intent(this, BtService.class);
            //non serve, con l' avvio del percorso setterò i dati dell' Application
            //serviceIntent,putExtraData("");
            startService(serviceIntent);
        }
    }

    private void requestBluetoothConnectPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                new AlertDialog.Builder(this)
                        .setTitle("Attivazione Bluetooth")
                        .setMessage("Il Bluetooth serve per trovare gli oggetti di interessi nelle vicinanze.")
                        .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT))
                        .setNegativeButton("cancella", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permesso Concesso", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(this, BtService.class);
                startService(serviceIntent);
            } else {
                Toast.makeText(this, "Permesso Rifiutato", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}