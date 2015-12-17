package com.paulina.sadowska.robotwirelesscontroller.Wifi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.paulina.sadowska.robotwirelesscontroller.ControllerFragment;
import com.paulina.sadowska.robotwirelesscontroller.R;
import com.paulina.sadowska.robotwirelesscontroller.ReceivedDataFragment;
import com.paulina.sadowska.robotwirelesscontroller.VelocityFragment;

public class WifiActivity extends AppCompatActivity {


    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 65536;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            MjpegFragment fragment = new MjpegFragment();
            transaction.replace(R.id.mjpeg_fragment, fragment);

            ControllerFragment controllerFragment = new ControllerFragment();
            transaction.replace(R.id.controller_wifi_fragment_frame_view, controllerFragment);


            VelocityFragment velocityFragment = new VelocityFragment();
            transaction.replace(R.id.velocity_wifi_fragment, velocityFragment);

            ReceivedDataFragment receivedDataFragment = new ReceivedDataFragment();
            transaction.replace(R.id.received_data_wifi_fragment, receivedDataFragment);

            transaction.commit();
        }
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    String ip_adr = data.getStringExtra("ip_adr");
                    int ip_port = data.getIntExtra("ip_port", 8080);
                    String ip_command = data.getStringExtra("ip_command");
                    SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ip_adr", ip_adr);
                    editor.putInt("ip_port", ip_port);
                    editor.putString("ip_command", ip_command);

                    editor.commit();

                    new RestartApp().execute();
                }
                break;
        }
    }
}
