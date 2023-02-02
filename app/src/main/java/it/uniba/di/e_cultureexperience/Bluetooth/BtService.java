package it.uniba.di.e_cultureexperience.Bluetooth;

import it.uniba.di.e_cultureexperience.R;
import android.Manifest;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.MostraOggettoDiInteresseActivity;
import it.uniba.di.e_cultureexperience.OggettoDiInteresse.OggettoDiInteresse;

public class BtService extends IntentService {
    private static final String TAG = "BtDevicesScanner";
    private BluetoothAdapter bluetoothAdapter;

    static String bt_id_found = null;

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

        NotificationChannel channel = new NotificationChannel("000", "nome", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("description");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //definisco il broadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
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
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                                    unregisterReceiver(receiver);
                                    onDestroy();
                                }
                            }
                            if (bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.cancelDiscovery();
                            }
                            bluetoothAdapter.startDiscovery();
                            break;

                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG, "Bluetooth turning on");
                            break;
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "scansione iniziata");
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            onDestroy();
                        }
                    }
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    bluetoothAdapter.startDiscovery();
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceHardwareAddress = bluetoothDevice.getAddress(); // MAC address
                    Log.d(TAG, deviceHardwareAddress);
                    //controllo se si tratta di un oggetto di interesse
                    controllaRisultatoScansione(deviceHardwareAddress);
                    break;
            }
        }
    };

    public void controllaRisultatoScansione(String id_bluetooth) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("oggetti")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OggettoDiInteresse oggettoDiInteresse = document.toObject(OggettoDiInteresse.class);
                            //Controllo se corrisponde al bluetooth id di un oggetto di interesse
                            if (oggettoDiInteresse.getBluetooth_id().equals(id_bluetooth) && !id_bluetooth.equals(bt_id_found)) {
                                bt_id_found = id_bluetooth;

                                Intent intent = new Intent(this, MostraOggettoDiInteresseActivity.class);
                                intent.putExtra("oggettoDiInteresse", oggettoDiInteresse);
                                intent.putExtra("scannerizzato", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "000")
                                        .setSmallIcon(R.drawable.ic_baseline_chevron_right_24)
                                        .setContentTitle(oggettoDiInteresse.getNome())
                                        .setContentText("Hai raggiunto: "+oggettoDiInteresse.getNome())
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(111, builder.build());

//                                Intent intent = new Intent(getApplicationContext(), MostraOggettoDiInteresseActivity.class);
//                                intent.putExtra("oggettoDiInteresse", oggettoDiInteresse);
//                                intent.putExtra("scannerizzato", true);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);

                                //stopSelf();
                            }
                        }
                    } else {
                        Log.e("Error", "ERRORE NELLA LETTURA DEL DB.", task.getException());
                    }
                });
    }

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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                onDestroy();
            }
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
        return START_NOT_STICKY;
    }
        @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }
}
