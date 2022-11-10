package it.uniba.di.e_cultureexperience.Bluetooth;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import it.uniba.di.e_cultureexperience.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.HashMap;
import java.util.Map;

//todo: cambiare nome di questa activity
public class MainActivity extends AppCompatActivity {

    //DA AGGIUNGERE A DASHBOARD_METE_ACTIVITY
    //QUANDO PRENDO L' ID BLUETOOTH DELL' OGGETTO TROVATO VEDO SE è DI UN OGGETTO DI INTERESSE

    BluetoothAdapter bluetoothAdapter;
    final int REQUEST_ENABLE_BT = 0;
    final int REQUEST_ENABLE_LOCATION = 1;
    final int PERMISSION_REQUEST_CODE = 2;
    final int ENABLE_BT_SCAN_CODE = 3;
    final int ENABLE_BT_CONNECT = 4;

    final String TAG = "BtDevicesScanner";

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
        if (!(getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) || bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non supportato", Toast.LENGTH_SHORT).show();
        } else {
            //se la versione è maggiore della 31 devo controllare il permesso BLUETOOTH_CONNECT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, ENABLE_BT_CONNECT);
                } else {
                    //controllo se il BT è attivo
                    if (bluetoothAdapter.isEnabled()) {
                        //controllo se il permesso BLUETOOTH_SCAN è granted
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, ENABLE_BT_CONNECT);
                        }
                        else{
                            //avvio il servizio
                            Intent serviceIntent = new Intent(this, BtService.class);
                            startService(serviceIntent);
                        }
                    } else {
                        //chiedo l' attivazione del BT
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
            } else {
                //per le altre versioni mi basta avere il permesso LOCATION
                if (!hasPermissions(getApplicationContext(), permessi)) {
                    //controllo se il BT è attico
                    if (bluetoothAdapter.isEnabled()) {
                        //avvio il servizio
                        bluetoothAdapter.startDiscovery();
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                    } else {
                        //chiedo l' attivazione
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                } else {
                    //chiedo permesso di posizione e successivamente controllo se il Bt è attivo
                    ActivityCompat.requestPermissions(this, permessi, REQUEST_ENABLE_LOCATION);
                }
            }
        }
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {
        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ENABLE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //controllo che il BT sia attivo
                    if (bluetoothAdapter.isEnabled()) {
                        //avvio il servizio
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                    } else {
                        //chiedo l' attivazione
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;

            case ENABLE_BT_CONNECT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (bluetoothAdapter.isEnabled()) {
                        //controllo di avere il permesso BLUETOOTH_SCAN
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, ENABLE_BT_CONNECT);
                            }
                        } else {
                            //avvio il servizio
                            bluetoothAdapter.startDiscovery();
                            Intent serviceIntent = new Intent(this, BtService.class);
                            startService(serviceIntent);
                        }
                    } else {
                        //chiedo l' attivazione
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;


//            case PERMISSION_REQUEST_CODE:
//                HashMap<String, Integer> permissionResults = new HashMap<>();
//                int deniedCount = 0;
//
//                for (int i = 0; i < grantResults.length; i++) {
//                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                        permissionResults.put(permissions[i], grantResults[i]);
//                        deniedCount++;
//                    }
//                }
//                if (deniedCount == 0) {
//                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
//                    if (bluetoothAdapter.isEnabled()) {
//                        Intent serviceIntent = new Intent(this, BtService.class);
//                        startService(serviceIntent);
//                    } else {
//                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                    }
//                } else {
//                    for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
//                        String permName = entry.getKey();
//                        int permResult = entry.getValue();
//                        Log.d("permessi", permName);
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
//                            new AlertDialog.Builder(this).setTitle("Permission")
//                                    .setCancelable(false)
//                                    .setMessage("Accettare i seguenti permessi per abilitare la scansione degli oggetti di interesse nelle vicinanzee")
//                                    .setPositiveButton("Procedi", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                            //askPermissions();
//                                        }
//                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                        }
//                                    }).create().show();
//                        } else {
//                            new AlertDialog.Builder(this).setTitle("PermissionDenied")
//                                    .setCancelable(false)
//                                    .setMessage("Abilita i permessi dalle impostazioni per usufruire dei servizi offerti")
//                                    .setPositiveButton("Vai alle impostazioni", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                                    Uri.fromParts("package", getPackageName(), null));
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    }).setNegativeButton("No, don' t allow", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                        }
//                                    }).create().show();
//                            break;
//                        }
//                    }
//                }
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    //controllo se il permesso BLUETOOTH_SCAN è granted
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, ENABLE_BT_CONNECT);
                    } else {
                        //avvio il servizio
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                    }
                }
                else{
                    //avvio il servizio
                    Intent serviceIntent = new Intent(this, BtService.class);
                    startService(serviceIntent);
                }
            } else {
                //attivazione bluetooth non concessa
                Toast.makeText(MainActivity.this, "Attivazione non concessa", Toast.LENGTH_LONG).show();
                finish();
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