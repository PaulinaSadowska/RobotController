package com.paulina.sadowska.robotwirelesscontroller.Wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paulina.sadowska.robotwirelesscontroller.R;

public class SettingsActivity extends Activity {

    Button settings_done;

    EditText address_input;
    EditText port_input;
    EditText command_input;

    String ip_adr = "192.168.1.170";
    int ip_port = 8080;
    String ip_command = "?action=stream";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_settings);

        Bundle extras = getIntent().getExtras();

        address_input = (EditText) findViewById(R.id.address_input);
        port_input = (EditText) findViewById(R.id.port_input);
        command_input = (EditText) findViewById(R.id.command_input);

        if (extras != null) {
            ip_adr = extras.getString("ip_adr", ip_adr);
            ip_port = extras.getInt("ip_port", ip_port);
            ip_command = extras.getString("ip_command");

            address_input.setText(String.valueOf(ip_adr));
            port_input.setText(String.valueOf(ip_port));
            command_input.setText(ip_command);
        }



        settings_done = (Button) findViewById(R.id.settings_done);
        settings_done.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        String s;

                        s = address_input.getText().toString();
                        ip_adr = s;

                        s = port_input.getText().toString();
                        if (!"".equals(s)) {
                            ip_port = Integer.parseInt(s);
                        }

                        s = command_input.getText().toString();
                        ip_command = s;

                        Intent intent = new Intent();
                        intent.putExtra("ip_adr", ip_adr);
                        intent.putExtra("ip_port", ip_port);
                        intent.putExtra("ip_command", ip_command);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );
    }
}
