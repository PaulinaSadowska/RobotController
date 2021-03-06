package com.paulina.sadowska.robotwirelesscontroller.Wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.paulina.sadowska.robotwirelesscontroller.Constants;
import com.paulina.sadowska.robotwirelesscontroller.ControllerFragment;
import com.paulina.sadowska.robotwirelesscontroller.MessageManager;
import com.paulina.sadowska.robotwirelesscontroller.R;
import com.paulina.sadowska.robotwirelesscontroller.ReceivedDataFragment;
import com.paulina.sadowska.robotwirelesscontroller.VelocityFragment;

public class WifiActivity extends AppCompatActivity {

    private ReceivedDataFragment receivedDataFragment;
    private ControllerFragment controllerFragment;



    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 65536;
    private String ip_adr = Constants.IP_ADDRESS_DEFAULT;
    private int ip_port_tcp = Constants.IP_PORT_TCP_DEFAULT;
    private int ip_port_camera = Constants.IP_PORT_CAMERA_DEFAULT;
    private Handler controlMessageThread;
    private WifiMessagesManager wifiManager;
    private MessageManager messageManager;
    private FrameLayout mjpegFragment;


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 400;
    private boolean mVisible;
    private final Handler mHideHandler = new Handler();

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final FrameLayout.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mjpegFragment.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            controllerFragment.fragmentDimensionsChanged();
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            controllerFragment.fragmentDimensionsChanged();
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    // Define the task to be run here
    private Runnable sendControlMessage = new Runnable() {
        @Override
        public void run() {
            if(wifiManager != null)
            {
                if(wifiManager.getConnectionState())
                {
                    if(!controllerFragment.getConnectionState())
                        controllerFragment.setConnectionState(true);

                    wifiManager.sendMessage(messageManager.getMessageText());
                    controlMessageThread.postDelayed(sendControlMessage, Constants.TIME_TO_SEND_CONTROL_MSG_MS);
                }
                else
                {
                    if(controllerFragment.getConnectionState())
                        controllerFragment.setConnectionState(false);
                    controlMessageThread.postDelayed(sendControlMessage, Constants.TIME_TO_CHECK_CONNECTION_STATE);
                }
            }
            else{
                if(controllerFragment.getConnectionState())
                    controllerFragment.setConnectionState(false);
                controlMessageThread.postDelayed(sendControlMessage, Constants.TIME_TO_CHECK_CONNECTION_STATE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            MjpegFragment fragment = new MjpegFragment();
            transaction.replace(R.id.mjpeg_fragment, fragment);

            controllerFragment = new ControllerFragment();
            transaction.replace(R.id.controller_wifi_fragment_frame_view, controllerFragment);


            VelocityFragment velocityFragment = new VelocityFragment();
            transaction.replace(R.id.velocity_wifi_fragment, velocityFragment);

            receivedDataFragment = new ReceivedDataFragment();
            transaction.replace(R.id.received_data_wifi_fragment, receivedDataFragment);

            transaction.commit();
        }

        controlMessageThread = new Handler();
        SharedPreferences preferences = getSharedPreferences(Constants.SAVED_VALUES_PREF_STR, MODE_PRIVATE);
        mjpegFragment = (FrameLayout) findViewById(R.id.mjpeg_fragment);

        ip_adr = preferences.getString(Constants.IP_ADRESS_STR, ip_adr);
        ip_port_tcp = preferences.getInt(Constants.IP_PORT_TCP_STR, ip_port_tcp);
        ip_port_camera = preferences.getInt(Constants.IP_PORT_CAMERA_STR, ip_port_camera);

        mjpegFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences(Constants.SAVED_VALUES_PREF_STR, MODE_PRIVATE);
        ip_adr = preferences.getString(Constants.IP_ADRESS_STR, ip_adr);
        ip_port_tcp = preferences.getInt(Constants.IP_PORT_TCP_STR, ip_port_tcp);
        ip_port_camera = preferences.getInt(Constants.IP_PORT_CAMERA_STR, ip_port_camera);

        messageManager = new MessageManager(new MessageManager.OnMessageReceived() {
            @Override
            public void messageReceived() {
                receivedDataFragment.bindData();
            }
        });
        if(wifiManager == null)
        {
            wifiManager = new WifiMessagesManager(messageManager, ip_adr, ip_port_tcp, this);
            wifiManager.connect();
        }
        else
        {
            wifiManager.disconnect();
            wifiManager = null;
            wifiManager = new WifiMessagesManager(messageManager, ip_adr, ip_port_tcp, this);
            wifiManager.connect();
        }
        Log.d("WifiActivity", "Wifi Activity STARTED");
        controlMessageThread.post(sendControlMessage);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wifiManager.disconnect();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wifi_menu, menu);
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


    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            WifiActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
            startActivity((new Intent(WifiActivity.this, WifiActivity.class)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("WifiActivity", "Wifi Activity FINISHED");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    String ip_adr = data.getStringExtra(Constants.IP_ADRESS_STR);
                    ip_port_camera = data.getIntExtra(Constants.IP_PORT_CAMERA_STR, ip_port_camera);
                    ip_port_tcp = data.getIntExtra(Constants.IP_PORT_TCP_STR, ip_port_tcp);

                    SharedPreferences preferences = getSharedPreferences(Constants.SAVED_VALUES_PREF_STR, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constants.IP_ADRESS_STR, ip_adr);
                    editor.putInt(Constants.IP_PORT_TCP_STR, ip_port_tcp);
                    editor.putInt(Constants.IP_PORT_CAMERA_STR, ip_port_camera);
                    editor.commit();

                    new RestartApp().execute();
                }
                break;
        }
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mjpegFragment.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
