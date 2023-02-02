package it.uniba.di.e_cultureexperience.Bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import it.uniba.di.e_cultureexperience.DashboardMeteActivity;
import it.uniba.di.e_cultureexperience.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

//todo: cambiare nome di questa activity
public class MainActivity extends AppCompatActivity {

    //DA AGGIUNGERE A DASHBOARD_METE_ACTIVITY
    //QUANDO PRENDO L' ID BLUETOOTH DELL' OGGETTO TROVATO VEDO SE è DI UN OGGETTO DI INTERESSE
    BluetoothAdapter bluetoothAdapter;
    final int REQUEST_ENABLE_BT = 0;
    final int REQUEST_ENABLE_BT_CONNECT = 1;
    final int REQUEST_ENABLE_LOCATION = 2;

    final String TAG = "BtDevicesScanner";

    final int permissionsCode = 33;

    final String[] PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Use this check to determine whether Bluetooth classic is supported on the device.
        if ((getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) && bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!hasPermissions(PERMISSIONS)) {
                    //se non sono granted li chiede
                    ActivityCompat.requestPermissions(this, PERMISSIONS, permissionsCode);
                } else {
                    //controllo che il bluetooth sia attivo
                    if (bluetoothAdapter.isEnabled()) {
                        //se attivo, avvia il servizio
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                        //lo mando alla dashboard
                        goToDashboard();
                    } else {
                        //se non attivo, chiedo l' attivazione
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, PERMISSIONS, permissionsCode);
                        }
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            }
//            } else {
//                //per le altre versioni mi basta avere il permesso LOCATION
//                if (!hasPermissions(PERMISSIONS)) {
//                    //controllo se il BT è attico
//                    if (bluetoothAdapter.isEnabled()) {
//                        //avvio il servizio
//                        Intent serviceIntent = new Intent(this, BtService.class);
//                        startService(serviceIntent);
//                        goToDashboard();
//                    } else {
//                        //chiedo l' attivazione
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                    }
//                } else {
//                    //chiedo permesso di posizione e successivamente controllo se il Bt è attivo
//                    ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ENABLE_LOCATION);
//                }
//            }
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            boolean z=true;
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    z= false;
                }else{
                    Log.d("PERMISSIONS", "Permission granted: " + permission);
                }
            }
            return z;
        }
        return false;
    }

    public void goToDashboard(){
        Intent i = new Intent(this, DashboardMeteActivity.class);
        startActivity(i);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == permissionsCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (bluetoothAdapter.isEnabled()) {
                    //avvio il servizio
                    Intent serviceIntent = new Intent(this, BtService.class);
                    startService(serviceIntent);
                    //lo mando alla dashboard
                    goToDashboard();
                } else {
                    //se Bluetooth non attivo, chiedo l' attivazione
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        //se non è granted chiede autorizzazione
                        ActivityCompat.requestPermissions(this, PERMISSIONS, permissionsCode);
                        return;
                    }
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
            else{
                goToDashboard();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //avvio il servizio
                Intent serviceIntent = new Intent(this, BtService.class);
                startService(serviceIntent);
            } else {
                //attivazione bluetooth non concessa
                Toast.makeText(MainActivity.this, "Attivazione non concessa", Toast.LENGTH_LONG).show();
            }
            goToDashboard();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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