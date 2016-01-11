package com.paulina.sadowska.robotwirelesscontroller.Bluetooth;

/**
 * Created by palka on 29.11.15.
 */
/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.paulina.sadowska.robotwirelesscontroller.ControllerFragment;
import com.paulina.sadowska.robotwirelesscontroller.MessageManager;
import com.paulina.sadowska.robotwirelesscontroller.R;
import com.paulina.sadowska.robotwirelesscontroller.ReceivedDataFragment;
import com.paulina.sadowska.robotwirelesscontroller.VelocityFragment;

public class BluetoothActivity extends AppCompatActivity implements BluetoothFragment.BluetoothActivityCallback {

    public static final String TAG = "BluetoothActivity";
    public MessageManager manager;
    ReceivedDataFragment receivedDataFragment;
    ControllerFragment controllerFragment;


    @Override
    public void onMessageReceivedCallback() {
        receivedDataFragment.bindData();
    }

    @Override
    public void onConnectionStateChangedCallback(boolean isConnected) {
        controllerFragment.setConnectionState(isConnected);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            BluetoothFragment fragment = new BluetoothFragment();
            transaction.replace(R.id.bluetooth_fragment, fragment);

            controllerFragment = new ControllerFragment();
            transaction.replace(R.id.controller_fragment_frame_view, controllerFragment);

            VelocityFragment velocityFragment = new VelocityFragment();
            transaction.replace(R.id.velocity_fragment, velocityFragment);

            receivedDataFragment = new ReceivedDataFragment();
            transaction.replace(R.id.received_data_fragment, receivedDataFragment);

            transaction.commit();
        }
        manager = new MessageManager(new MessageManager.OnMessageReceived() {
            @Override
            public void messageReceived() {
                receivedDataFragment.bindData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}