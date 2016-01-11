package com.paulina.sadowska.robotwirelesscontroller;

import com.paulina.sadowska.robotwirelesscontroller.Bluetooth.BluetoothService;

/**
 * Defines several constants used between {@link BluetoothService} and the UI.
 */
public interface Constants {

    int TIME_TO_SEND_CONTROL_MSG_MS = 100; //time in ms

    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";

    char START_BYTE = '{';
    char STOP_BYTE = '}';
    char MINUS_SIGN = '-';
    char PLUS_SIGN = '+';

    String START_BYTE_STR = "{";
    String STOP_BYTE_STR = "}";
    int INPUT_MESSAGE_LENGTH = 12;

    int INPUT_CONNECTION_MESSAGE_LENGTH = 3;

    int INDEX_CONNECTION_INFO = 1;

    String CONNECTION_SETUP_MESSAGE = "{C}";
    String CONNECTION_STOP_MESSAGE = "{D}";
    char CONNECTION_ACK_BYTE = 'A';
    char CONNECTION_ERROR_BYTE = 'E';

    //second format
    String ROBOT_STOP_STR = "S"; //STOP ROBOT
    String ROBOT_MOVE_STR = "M"; //MOVE ROBOT

//*WIFI CONSTANTS */

    String HTTP_STRING = "http://";
    String COLON_STRING = ":";
    String SLASH_STRING = "/";

    //settings wifi preferences constant strings
    String IP_ADRESS_STR = "ip_addr";
    String IP_PORT_CAMERA_STR = "ip_port_camera";
    String IP_PORT_TCP_STR = "ip_port_tcp";

}
