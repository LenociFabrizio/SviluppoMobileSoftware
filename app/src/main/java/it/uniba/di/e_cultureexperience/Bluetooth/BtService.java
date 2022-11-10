package it.uniba.di.e_cultureexperience.Bluetooth;

import android.Manifest;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class BtService extends IntentService {
    private static final String TAG = "BtDevicesScanner";
    private BluetoothAdapter bluetoothAdapter;

    public BtService(String name) {
        super(name);
        setIntentRedelivery(false);
    }

    public BtService() {
        super("Default");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //definisco il broadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)){
                Log.d(TAG, "avvio scansione");
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();
            }else{
                Toast.makeText(getApplicationContext(), "Impossibile continuare la scansione Bluetooth, controllare i permessi", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        }
        else{
            Log.d(TAG, "avvio scansione");
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                if((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)){
                                    Log.d(TAG, "avvio scansione");
                                    if (bluetoothAdapter.isDiscovering()) {
                                        bluetoothAdapter.cancelDiscovery();
                                    }
                                    bluetoothAdapter.startDiscovery();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Impossibile continuare la scansione Bluetooth, controllare i permessi", Toast.LENGTH_SHORT).show();
                                    stopSelf();
                                }
                            }
                            else{
                                Log.d(TAG, "avvio scansione");
                                if (bluetoothAdapter.isDiscovering()) {
                                    bluetoothAdapter.cancelDiscovery();
                                }
                                bluetoothAdapter.startDiscovery();
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)){
                            Log.d(TAG, "avvio scansione");
                            if (bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.cancelDiscovery();
                            }
                            bluetoothAdapter.startDiscovery();
                        }else{
                            Toast.makeText(getApplicationContext(), "Impossibile continuare la scansione Bluetooth, controllare i permessi", Toast.LENGTH_SHORT).show();
                            stopSelf();
                        }
                    }
                    else{
                        Log.d(TAG, "avvio scansione");
                        if (bluetoothAdapter.isDiscovering()) {
                            bluetoothAdapter.cancelDiscovery();
                        }
                        bluetoothAdapter.startDiscovery();
                    }
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    Log.d(TAG, "Dispositivo trovato");
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = bluetoothDevice.getName();
                    String deviceHardwareAddress = bluetoothDevice.getAddress(); // MAC address
                    Log.d(TAG, "Dispositivo trovato: " + bluetoothDevice.getAddress());
                    Log.d(TAG, "Dispositivo trovato: " + bluetoothDevice.getName());
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "OnDestroy");
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartCommand");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)){
                Log.d(TAG, "avvio scansione");
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();
            }else{
                Toast.makeText(this, "Impossibile continuare la scansione Bluetooth, controllare i permessi", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        }
        else{
            Log.d(TAG, "avvio scansione");
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }
}
