package com.rn_tsc_printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class BlutoothModule extends ReactContextBaseJavaModule {
    private static final Integer REQUEST_ENABLE_BT = 1;

    BlutoothModule(ReactApplicationContext context) {
        super(context);
    }

    BluetoothManager bluetoothManager = getReactApplicationContext().getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

    @ReactMethod
    public void checkBluetoothEnabled() {
        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getReactApplicationContext().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
