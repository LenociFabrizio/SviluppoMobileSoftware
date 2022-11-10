package it.uniba.di.e_cultureexperience.Bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import it.uniba.di.e_cultureexperience.R;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Map;

//todo: cambiare nome di questa activity
public class copia extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;

    final int REQUEST_ENABLE_BT = 0;
    final int REQUEST_ENABLE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permessi = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Use this check to determine whether Bluetooth classic is supported on the device.
        if ( !(getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ) || bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non supportato", Toast.LENGTH_SHORT).show();
        }
        else{
            //se la versione è maggiore della 31 devo controllare il permesso BLUETOOTH_CONNECT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!hasPermissions(getApplicationContext(), permessi)) {
                    if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) && bluetoothAdapter.isEnabled()) {
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                    }
                    else{
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
                else{
                    //chiedo permesso di posizione e successivamente controllo se il Bt è attivo
                    Log.d("permessi","aaa");
                    ActivityCompat.requestPermissions(this, permessi, REQUEST_ENABLE_LOCATION);
                }
            }
            else {
                if (!hasPermissions(getApplicationContext(), permessi)) {
                    if(bluetoothAdapter.isEnabled()){
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                    }
                    else{
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
                else{
                    //chiedo permesso di posizione e successivamente controllo se il Bt è attivo
                    Log.d("permessi","aaa");
                    ActivityCompat.requestPermissions(this, permessi, REQUEST_ENABLE_LOCATION);
                }
            }
        }
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS){
        if(context != null && PERMISSIONS != null){
            for(String permission : PERMISSIONS){
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_ENABLE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    if(bluetoothAdapter.isEnabled()){
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                    }
                    else{
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                       // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                } else{
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;

//            case REQUEST_ENABLE_COARSE_LOCATION:
//                Log.d("richieste", "REQUEST_ENABLE_COARSE_LOCATION");
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //Toast.makeText(this, "Permesso Concesso", Toast.LENGTH_SHORT).show();
//                    //continuo con l' avvio del service
//                    Intent serviceIntent = new Intent(this, BtService.class);
//                    //////////
//                    serviceIntent.putExtra("permesso", grantResults[0]);
//                    startService(serviceIntent);
//                } else{
//                    Toast.makeText(this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT).show();
//                    //ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.BLUETOOTH }, REQUEST_ENABLE_BT);
//                }
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            Log.d("richieste", "REQUEST_ENABLE_BT dentro onActivityResult");
            if (resultCode == RESULT_OK) {
                //attivazione bluetooth concessa
                //Toast.makeText(MainActivity.this, "Attivazione Bluetooth", Toast.LENGTH_LONG).show();
                //è abilitato e continuo con l' avvio del servizio
                Intent serviceIntent = new Intent(this, BtService.class);
                startService(serviceIntent);
            } else if (resultCode == RESULT_CANCELED) {
                //attivazione bluetooth non concessa
                //Toast.makeText(MainActivity.this, "Attivazione non concessa", Toast.LENGTH_LONG).show();
                //finish();
            }
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