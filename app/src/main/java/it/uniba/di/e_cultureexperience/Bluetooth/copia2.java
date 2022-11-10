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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//todo: cambiare nome di questa activity
public class copia2 extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    final int REQUEST_ENABLE_BT = 0;
    final int REQUEST_ENABLE_LOCATION = 1;
    final int PERMISSION_REQUEST_CODE = 2;
    final int ENABLE_BT_SCAN_CODE = 3;
    final int ENABLE_BT_CONNECT = 4;

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
                if (!hasPermissions(getApplicationContext(), permessi)) {
                    //controllo il BLUETOOTH_CONNECT
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, permessi, ENABLE_BT_CONNECT);
                    }else{
                        if (bluetoothAdapter.isEnabled()) {
                            bluetoothAdapter.startDiscovery();
                            Intent serviceIntent = new Intent(this, BtService.class);
                            startService(serviceIntent);
                        } else {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
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
                        bluetoothAdapter.startDiscovery();
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
                    ActivityCompat.requestPermissions(this, permessi, REQUEST_ENABLE_LOCATION);
                }
            }
        }
    }

//    public boolean askPermissions(){
//        List<String> listPermissionNeeded = new ArrayList<>();
//        for(String perm: permessiN){
//            if(ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED){
//                Log.d("permesi",perm);
//                listPermissionNeeded.add(perm);
//            }
//        }
//        if(!listPermissionNeeded.isEmpty()){
//            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), PERMISSION_REQUEST_CODE);
//            return false;
//        }
//        return true;
//    }

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
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                } else{
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSION_REQUEST_CODE:
                HashMap<String, Integer> permissionResults = new HashMap<>();
                int deniedCount = 0;

                for(int i = 0; i< grantResults.length; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        permissionResults.put(permissions[i], grantResults[i]);
                        deniedCount++;
                    }
                }
                if(deniedCount==0){
                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                    if(bluetoothAdapter.isEnabled()){
                        Intent serviceIntent = new Intent(this, BtService.class);
                        startService(serviceIntent);
                    }
                    else{
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }else{
                    for(Map.Entry<String,Integer> entry : permissionResults.entrySet()){
                        String permName = entry.getKey();
                        int permResult = entry.getValue();
                        Log.d("permessi", permName);
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,permName)){
                            new AlertDialog.Builder(this).setTitle("Permission")
                                    .setCancelable(false)
                                    .setMessage("Accettare i seguenti permessi per abilitare la scansione degli oggetti di interesse nelle vicinanzee")
                                    .setPositiveButton("Procedi", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            //askPermissions();
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create().show();
                        }
                        else{
                            new AlertDialog.Builder(this).setTitle("PermissionDenied")
                                    .setCancelable(false)
                                    .setMessage("Abilita i permessi dalle impostazioni per usufruire dei servizi offerti")
                                    .setPositiveButton("Vai alle impostazioni", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package", getPackageName(), null));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).setNegativeButton("No, don' t allow", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create().show();
                            break;
                        }
                    }
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