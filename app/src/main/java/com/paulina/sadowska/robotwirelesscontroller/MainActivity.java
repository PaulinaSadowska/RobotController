package com.paulina.sadowska.robotwirelesscontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.paulina.sadowska.robotwirelesscontroller.Bluetooth.BluetoothActivity;
import com.paulina.sadowska.robotwirelesscontroller.Wifi.WifiActivity;

public class MainActivity extends AppCompatActivity {

    private Button mWifiButton;
    private Button mBluetoothButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiButton = (Button)findViewById(R.id.main_activity_wifi_button);
        mBluetoothButton = (Button) findViewById(R.id.main_activity_bluetooth_button);
        mWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WifiActivity.class);
                startActivity(intent);
            }
        });

        mBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BluetoothActivity.class);
                startActivity(intent);
            }
        });

    }

}
