//package it.uniba.di.e_cultureexperience.Bluetooth;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.core.app.ActivityCompat;
//
//import it.uniba.di.e_cultureexperience.Accesso.ProfileActivity;
//
//public class BroadcastReceiver_DevicesFound extends BroadcastReceiver {
//    // Create a BroadcastReceiver for ACTION_FOUND.
//    public void onReceive(Context context, Intent intent) {
//        final String action = intent.getAction();
//        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//            // Discovery has found a device. Get the BluetoothDevice
//            // object and its info from the Intent.
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                String deviceName = device.getName();
//                Log.d("TEST", deviceName);
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.d("TEST", deviceHardwareAddress);
//            }
//            else{
//                requestBluetoothPermission();
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//            }
//        }
//    }
//
//    private void requestBluetoothPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.CAMERA)) {
//
//            new AlertDialog.Builder(this)
//                    .setTitle("Concedi permesso")
//                    .setMessage("Questo permesso serve per poter accedere alla fotocamera per impostare la propria foto profilo.")
//                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(ProfileActivity.this,
//                            new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE))
//                    .setNegativeButton("cancella", (dialog, which) -> dialog.dismiss())
//                    .create().show();
//
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//        }
//    }
//
//    @SuppressLint("MissingSuperCall")
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == CAMERA_PERMISSION_CODE)  {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permesso Concesso", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Permesso Rifiutato", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//}
