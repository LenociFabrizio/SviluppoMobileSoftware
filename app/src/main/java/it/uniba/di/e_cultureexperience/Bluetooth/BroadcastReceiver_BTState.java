package it.uniba.di.e_cultureexperience.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcastReceiver_BTState extends BroadcastReceiver {

    Context activityContext;

    public BroadcastReceiver_BTState(Context activityContext) {
        this.activityContext = activityContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(activityContext, "Bluetooth spento", Toast.LENGTH_LONG).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(activityContext, "Disattivando il Bluetooth", Toast.LENGTH_LONG).show();
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(activityContext, "Bluetooth attivo", Toast.LENGTH_LONG).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(activityContext, "Attivando il Bluetooth", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        /*
        if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            Log.d("TEST", deviceName);
            String deviceHardwareAddress = device.getAddress(); // MAC address
            Log.d("TEST", deviceHardwareAddress);
        }
         */
    }
}