package com.paulina.sadowska.robotwirelesscontroller.Wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paulina.sadowska.robotwirelesscontroller.Constants;
import com.paulina.sadowska.robotwirelesscontroller.R;

public class SettingsActivity extends Activity {

    Button settings_done;

    EditText address_input;
    EditText port_camera_input;
    EditText port_tcp_input;
    Context context;

    String ip_adr = Constants.IP_ADDRESS_DEFAULT;
    int ip_port_camera = Constants.IP_PORT_CAMERA_DEFAULT;
    int ip_port_tcp = Constants.IP_PORT_TCP_DEFAULT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_settings);

        Bundle extras = getIntent().getExtras();

        address_input = (EditText) findViewById(R.id.address_input);
        port_camera_input = (EditText) findViewById(R.id.port_input_camera);
        port_tcp_input = (EditText) findViewById(R.id.port_input_tcp);

        if (extras != null) {
            ip_adr = extras.getString(Constants.IP_ADRESS_STR, ip_adr);
            ip_port_camera = extras.getInt(Constants.IP_PORT_CAMERA_STR, ip_port_camera);
            ip_port_tcp = extras.getInt(Constants.IP_PORT_TCP_STR, ip_port_tcp);

            address_input.setText(String.valueOf(ip_adr));
            port_camera_input.setText(String.valueOf(ip_port_camera));
            port_tcp_input.setText(String.valueOf(ip_port_tcp));
        }

        context = this;

        settings_done = (Button) findViewById(R.id.settings_done);
        settings_done.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        String s;

                        s = address_input.getText().toString();
                        ip_adr = s;

                        s = port_camera_input.getText().toString();
                        if (!"".equals(s)) {
                            ip_port_camera = Integer.parseInt(s);
                        }

                        s = port_tcp_input.getText().toString();
                        if (!"".equals(s)) {
                            ip_port_tcp = Integer.parseInt(s);
                        }

                        Intent intent = new Intent();
                        if(ip_port_camera>65535)
                        {
                            ip_port_camera = 65535;
                            Toast.makeText(context, "camera port number not valid", Toast.LENGTH_SHORT).show();
                        }

                        if(ip_port_tcp>65535)
                        {
                            ip_port_tcp = 65535;
                            Toast.makeText(context, "tcp port number not valid", Toast.LENGTH_SHORT).show();
                        }

                        intent.putExtra(Constants.IP_ADRESS_STR, ip_adr);
                        intent.putExtra(Constants.IP_PORT_CAMERA_STR, ip_port_camera);
                        intent.putExtra(Constants.IP_PORT_TCP_STR, ip_port_tcp);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );
    }
}
