package com.paulina.sadowska.robotwirelesscontroller.Wifi;

/**
 * Created by palka on 10.09.15.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {


    private String serverMessage;                           //recieved message
    private String SERVERIP = "192.168.1.170";  //IP address of server on Raspberry PI
    private int SERVERPORT = 8080;              //port on which Raspberry PI is listening
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;                           //as long mRun is true client is working

    PrintWriter out;    //out buffer
    BufferedReader in;  //in buffer

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, String serverIP, int port) {
        mMessageListener = listener;
        SERVERIP = serverIP;
        SERVERPORT = port;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public boolean getConnectionState(){
        return mRun;
    }

    /**
     * Function used to stop the client
     */
    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.d("TCP Client", "C: Sent.");

                Log.d("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mMessageListener.messageReceived("Connection estabilished");

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                        serverMessage = in.readLine();

                        if (serverMessage!=null && mMessageListener != null)
                        {
                            //call the method messageReceived from MyActivity class
                            mMessageListener.messageReceived(serverMessage);

                            Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
                        }
                        serverMessage = "";
                }

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
                mMessageListener.messageReceived("Connection lost");
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);
            mMessageListener.messageReceived("error");
        }
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asyncTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);

    }
}
