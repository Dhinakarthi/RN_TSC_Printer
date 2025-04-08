package com.rn_tsc_printer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.tscdll.TSCActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.Set;

public class BlutoothModule extends ReactContextBaseJavaModule {
    BluetoothManager bluetoothManager = getReactApplicationContext().getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    private static final Integer REQUEST_ENABLE_BT = 1;

    private TSCActivity bt_api;

    @Override
    public void initialize(){
        super.initialize();
        bt_api = new TSCActivity();
    }

    @ReactMethod
    public void TSCPrintLabel(){
        bt_api.openport("34:81:F4:9C:DE:53");
        String printer_status = bt_api.status();
        Log.v("printer_status", printer_status);

        bt_api.sendcommand("SIZE 75 mm, 50 mm\r\n");
        bt_api.clearbuffer();
        bt_api.sendcommand("SPEED 4\r\n");
        bt_api.sendcommand("DENSITY 12\r\n");
        bt_api.sendcommand("CODEPAGE UTF-8\r\n");
        bt_api.sendcommand("SET TEAR ON\r\n");
        bt_api.sendcommand("SET COUNTER @1 1\r\n");
        bt_api.sendcommand("@1 = \"0001\"\r\n");
        bt_api.sendcommand("TEXT 100,300,\"ROMAN.TTF\",0,12,12,@1\r\n");
        bt_api.sendcommand("TEXT 100,400,\"ROMAN.TTF\",0,12,12,\"TEST FONT\"\r\n");
        bt_api.barcode(100, 100, "128", 100, 1, 0, 3, 3, "123456789");
        bt_api.printerfont(100, 250, "3", 0, 1, 1, "987654321");
        bt_api.printlabel(2, 1);

        bt_api.closeport(5000);
        Toast.makeText(getReactApplicationContext(), "Status: "+printer_status, Toast.LENGTH_LONG).show();

    }

    BlutoothModule(ReactApplicationContext context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @ReactMethod
    public void checkBluetoothEnabled() {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(currentActivity, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            // Optionally request the permission here if your app targets Android 12+
            ActivityCompat.requestPermissions(
                    currentActivity,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                    101 // Request code you can handle in onRequestPermissionsResult
            );
            return;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            currentActivity.startActivityForResult(enableBtIntent, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @ReactMethod
    public void getPairedDevices(Promise promise){
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            promise.reject("NO_ACTIVITY", "Current activity is null");
            return;
        }

        if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    currentActivity,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                    101 // Request code you can handle in onRequestPermissionsResult
            );
            promise.reject("PERMISSION_DENIED", "Bluetooth connect permission denied");
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        WritableArray deviceList = Arguments.createArray();

        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                WritableMap deviceMap = Arguments.createMap();
                deviceMap.putString("name", device.getName());
                deviceMap.putString("address", device.getAddress());
                deviceList.pushMap(deviceMap);
            }
        }

        promise.resolve(deviceList);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @ReactMethod
    public void startDiscovery(){
        Activity currentActivity = getCurrentActivity();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()){
            if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                assert currentActivity != null;
                ActivityCompat.requestPermissions(
                        currentActivity,
                        new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        101 // Request code you can handle in onRequestPermissionsResult
                );
                return;
            }
            Log.v("Dhinaaaa", "Dhiana");
            bluetoothAdapter.startDiscovery();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @ReactMethod
    public void stopDiscovery() {
        Activity currentActivity = getCurrentActivity();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            assert currentActivity != null;
            ActivityCompat.requestPermissions(
                    currentActivity,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                    101 // Request code you can handle in onRequestPermissionsResult
            );
            return;
        }
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @ReactMethod
    public boolean discoveryStatus(){
        if (bluetoothAdapter != null){
            if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false;
            }
            return bluetoothAdapter.isDiscovering();
        }
        return false;
    };

    @ReactMethod
    public String checkBluetoothAdapter() {
        if (bluetoothAdapter == null) {
            return "Bluetooth Adapter not found";
        } else {
            return "Bluetooth Adapter is available";
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "BlutoothModule";
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void sampleToast(String message) {
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @ReactMethod
    public boolean bluetoothAvailable() {
        return getReactApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    @ReactMethod
    public boolean bluetoothLEAvailable() {
        return getReactApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }
}
