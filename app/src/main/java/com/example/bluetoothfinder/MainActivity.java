package com.example.bluetoothfinder;

/* Note: COARSE/FINE LOCATION needs to be added in Manifest file because of the Marshmallow permission
 * You can directly allow the location for the app from phone or add marshmallow permission code to the project
 *
 * To display the list of devices that has bluetooth on, in the app(project) itself, we will add the array adapter to project
 * ArrayAdapter converts an ArrayList of objects into View items loaded into the ListView container.*/

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button searchButton;
    ListView listView;

    // BluetoothAdapter is an inbuilt class that communicates with bluetooth
    BluetoothAdapter bluetoothAdapter;

    ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("Searching...");
                searchButton.setEnabled(false);
                bluetoothAdapter.startDiscovery();
            }
        });

        // initializing arrayAdapter with context, layout and the arraylist
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bluetoothDevices);
        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("Action", action);

            // setting the search button to true when bluetooth searching is completed
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

//                Log.e("Finished", "inside finished");
                textView.setText("Finished");
                searchButton.setEnabled(true);
            }

            // if the device finds other devices that has bluetooth on, then we will log the message
             if (BluetoothDevice.ACTION_FOUND.equals(action)) {

//                Log.e("Found found", "inside found ");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));

//                Log.e("Device Found", "Name: " + name + "\nAddress: " + address + "\nRSSI: " + rssi );

                 // displaying the list of bluetooth devices to the app
                 if (name == null || name.equals("")) {
                     bluetoothDevices.add(address + " - RSSI" + rssi + "dBm");
                 }
                 else {
                     bluetoothDevices.add(name + " - RSSI" + rssi + "dBm");
                 }

                 // update the adapter
                 arrayAdapter.notifyDataSetChanged();
            }

        }
    };
}
