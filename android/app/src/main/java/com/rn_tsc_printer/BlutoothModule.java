package com.rn_tsc_printer;

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

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.Set;

public class BlutoothModule extends ReactContextBaseJavaModule {
    private static final Integer REQUEST_ENABLE_BT = 1;

    BlutoothModule(ReactApplicationContext context) {
        super(context);
    }

    BluetoothManager bluetoothManager = getReactApplicationContext().getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

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
                        new String[]{android.Manifest.permission.BLUETOOTH_CONNECT},
                        101 // Request code you can handle in onRequestPermissionsResult
                );
                return;
            }
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
