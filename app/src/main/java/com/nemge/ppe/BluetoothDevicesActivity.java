package com.nemge.ppe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothDevicesActivity extends AppCompatActivity {
    private static final String TAG = "BTActivity";

    Button enable_disable, discover, discoverable, connection, send;
    EditText dataBT;

    BluetoothAdapter bluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;

    ListView lvNewDevices;

    private static final UUID appID = UUID.fromString("eb70d6fe-0a74-433b-b3ac-53b9e8e7b116");
    BluetoothDevice mBTDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        setSupportActionBar(findViewById(R.id.home_toolbar));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        enable_disable = findViewById(R.id.On_Off);
        discover = findViewById(R.id.Discover);
        discoverable = findViewById(R.id.enable_disc);
        connection = findViewById(R.id.start_connect);

        dataBT = findViewById(R.id.dataBT);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        enable_disable.setOnClickListener(v -> {
            Log.d(TAG, "enabling/disabling Bluetooth");
            enableDisableBT();
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    public void Connection(){
        startBTConnection(mBTDevice, appID);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "Initializing Bluetooth Connection.");
        mBluetoothConnection.startClient(device, uuid);
    }

    public void enableDisableBT(){
        if(bluetoothAdapter == null){
            Log.d(TAG, "Does not have BT capabilities.");
        }

        if(bluetoothAdapter.isEnabled()){
            Log.d(TAG, "Enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver, BTIntent);
        }

        if(bluetoothAdapter.isEnabled()){
            Log.d(TAG, "Disabling BT.");
            bluetoothAdapter.disable();
        }
    }
}
