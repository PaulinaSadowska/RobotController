package com.paulina.sadowska.robotwirelesscontroller.Wifi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.paulina.sadowska.robotwirelesscontroller.Constants;
import com.paulina.sadowska.robotwirelesscontroller.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MjpegFragment extends Fragment {
    private static final boolean DEBUG = false;
    private static final String TAG = "MJPEG";
    InputStream is = null;

    private MjpegView mv = null;
    String URL;

    private int width = Constants.CAMERA_WIDTH;
    private int height = Constants.CAMERA_HEIGHT;

    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 0;

    private String ip_adr = Constants.IP_ADDRESS_DEFAULT;
    private int ip_port_tcp = Constants.IP_PORT_TCP_DEFAULT;
    private int ip_port_camera = Constants.IP_PORT_CAMERA_DEFAULT;
    private String ip_command = Constants.IP_CAMERA_COMMAND_DEFAULT;

    private boolean suspending = false;

    final Handler handler = new Handler();


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mjpeg_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SAVED_VALUES_PREF_STR, getActivity().MODE_PRIVATE);
                ip_adr = preferences.getString(Constants.IP_ADRESS_STR, ip_adr);
                ip_port_tcp = preferences.getInt(Constants.IP_PORT_TCP_STR, ip_port_tcp);
                ip_port_camera = preferences.getInt(Constants.IP_PORT_CAMERA_STR, ip_port_camera);

                Intent settings_intent = new Intent(getActivity(), SettingsActivity.class);
                settings_intent.putExtra(Constants.IP_ADRESS_STR, ip_adr);
                settings_intent.putExtra(Constants.IP_PORT_TCP_STR, ip_port_tcp);
                settings_intent.putExtra(Constants.IP_PORT_CAMERA_STR, ip_port_camera);
                startActivityForResult(settings_intent, REQUEST_SETTINGS);
                return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mjpeg_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.SAVED_VALUES_PREF_STR, getActivity().MODE_PRIVATE);
        ip_adr = preferences.getString(Constants.IP_ADRESS_STR, ip_adr);
        ip_port_tcp = preferences.getInt(Constants.IP_PORT_TCP_STR, ip_port_tcp);
        ip_port_camera = preferences.getInt(Constants.IP_PORT_CAMERA_STR, ip_port_camera);

        StringBuilder sb = new StringBuilder();
        sb.append(Constants.HTTP_STRING);
        sb.append(ip_adr);
        sb.append(Constants.COLON_STRING);
        sb.append(ip_port_camera);
        sb.append(Constants.SLASH_STRING);
        sb.append(ip_command);
        URL = new String(sb);

        mv = (MjpegView) getActivity().findViewById(R.id.mv);
        if (mv != null) {
            mv.setResolution(width, height);
        }

        setStatus(R.string.title_connecting_to_camera);
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DoRead().execute(URL);
        } else {
            setStatus(R.string.title_not_connected_to_camera);
        }
    }

    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        super.onResume();
        if (mv != null) {
            if (suspending) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new DoRead().execute(URL);
                } else {
                    setStatus(R.string.title_not_connected_to_camera);
                }
                suspending = false;
            }
        }

    }

    public void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
        super.onPause();
        if (mv != null) {
            if (mv.isStreaming()) {
                mv.stopPlayback();
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                suspending = true;
            }
        }
    }


    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");

        if (mv != null) {
            mv.freeCameraMemory();
        }

        super.onDestroy();
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {


        protected MjpegInputStream doInBackground(String... url) {
            //camera dont have authentication
            try {
                return new MjpegInputStream(downloadUrl(url[0]));
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if (result != null) {
                result.setSkip(3);
                setStatus(R.string.app_name);
            } else {
                setStatus(R.string.title_not_connected_to_camera);
            }
            int w = mv.getWidth();
            int h = 3 * w / 4;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, h);
            layoutParams.setMargins(5, 5, w, h);
            mv.setLayoutParams(layoutParams);
        }


    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private InputStream downloadUrl(String myurl) throws IOException {
        // Only display the first 500 characters of the retrieved
        // web page content.


            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("debug", "The response is: " + response);
            is = conn.getInputStream();
            return is;

            /*
            // TODO - Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();
            }*/
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getSupportActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getSupportActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

}
