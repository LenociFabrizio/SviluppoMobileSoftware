package it.uniba.di.e_cultureexperience.Bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;

public class BtService extends IntentService {
    private static final String TAG = "BtDevicesScanner";
    private PowerManager.WakeLock wakeLock;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver btReceiver;
    private final int BLUETOOTH_SCAN_PERMISSION_CODE = 1;

    public BtService(String name) {
        super(name);
        setIntentRedelivery(false);
    }

    public BtService(){
        super("Default");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*
            permette di tenere il servizio attivo anche con lo schermo bloccato
            PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ECulture: WakeLock");
            wakeLock.acquire(26000);
            Log.d(TAG,"WakeLock acquisito");
        */

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //definisco il broadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btReceiver = new BluetoothReceiver();
        registerReceiver(btReceiver, filter);

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
        //wakeLock.release();
        //Log.d(TAG,"Weaklock released");
        unregisterReceiver(btReceiver);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG,"OnStartCommand");

        //prendo l' indirizzo del dispositivo da trovare
        //String btDeviceAddress = intent.getStringExtra("btDeviceAddressToFind");

        //controllo che il Bluetooth sia attivo
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }
        else
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothAdapter.startDiscovery();
            } else {
                requestBluetoothScanPermission();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }

    public class BluetoothReceiver extends BroadcastReceiver {

        public BluetoothReceiver(){
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG, "Bluetooth off");
                            break;

                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d(TAG, "Bluetooth turning off");
                            break;

                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG, "Bluetooth on");
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                                bluetoothAdapter.startDiscovery();
                            } else {
                                requestBluetoothScanPermission();
                            }
                            break;

                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG, "Bluetooth turning on");
                            break;
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "ricerca iniziata");
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "Restart della scansione");
                    bluetoothAdapter.startDiscovery();
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //if( btDeviceAddress.equals(bluetoothDevice.getAddress()) )
                    //{
                    //TROVATO QUELLO CHE CERCAVO
                    //}
                    Log.d(TAG, "Dispositivo trovato: " + bluetoothDevice.getAddress());
                    Log.d(TAG, "Dispositivo trovato: " + bluetoothDevice.getName());
                    break;
            }
        }
    };

    private void requestBluetoothScanPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.BLUETOOTH_SCAN)) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                new AlertDialog.Builder(this)
//                        .setTitle("Concedi permesso")
//                        .setMessage("Questo permesso serve per poter scannerizzare i dispositivi Bluetooth nelle vicinanze.")
//                        .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(ProfileActivity.this,
//                                new String[] {Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN_PERMISSION_CODE))
//                        .setNegativeButton("cancella", (dialog, which) -> dialog.dismiss())
//                        .create().show();
//            }
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[] {Manifest.permission.CAMERA}, BLUETOOTH_SCAN_PERMISSION_CODE);
//        }
    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BLUETOOTH_SCAN_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permesso Concesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permesso Rifiutato", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
