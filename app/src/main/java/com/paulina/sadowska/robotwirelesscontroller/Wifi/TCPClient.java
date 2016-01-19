package com.paulina.sadowska.robotwirelesscontroller.Wifi;

/**
 * Created by palka on 10.09.15.
 */

import android.util.Log;

import com.paulina.sadowska.robotwirelesscontroller.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {


    private String serverMessage;                           //recieved message
    private String SERVER_IP = Constants.IP_ADDRESS_DEFAULT;  //IP address of server on Raspberry PI
    private int SERVER_PORT = Constants.IP_PORT_TCP_DEFAULT;              //port on which Raspberry PI is listening
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;                           //as long mRun is true client is working

    PrintWriter out;    //out buffer
    BufferedReader in;  //in buffer
    private Socket TCPsocket;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, String serverIP, int port) {
        mMessageListener = listener;
        SERVER_IP = serverIP;
        SERVER_PORT = port;
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
        try{
            if(TCPsocket!=null)
            {
                TCPsocket.close();
                Log.d("TCP Client", "C: socket closed");
            }
            else
                Log.d("TCP Client", "C: there is no socket to close");
        }
        catch (Exception e)
        {
            Log.d("TCP Client", "C: cannot close socket");
        }
    }

    public void run() {

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.d("TCP Client", "C: Connecting to ..." + SERVER_IP + " port: " + SERVER_PORT);
            //create a socket to make the connection with the server
            int timeout = 1000;
            if(TCPsocket == null)
                TCPsocket = new Socket();
            else if (!TCPsocket.isClosed())
                TCPsocket.close();

            if(!TCPsocket.isConnected())
                TCPsocket.connect(new InetSocketAddress(serverAddr, SERVER_PORT), timeout); //
            else
                Log.d("TCP Client", "S: socket already connected");

            Log.d("TCP Client", "S: socket created: " + SERVER_IP + " port: " + SERVER_PORT);


            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(TCPsocket.getOutputStream())), true);

                Log.d("TCP Client", "C: Sent.");

                Log.d("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(TCPsocket.getInputStream()));
                mMessageListener.messageReceived(Constants.CONNECTION_ESTABLISHED_MSG);
                mRun = true;

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
                mRun = false;

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
                mRun = false;

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                if(TCPsocket!=null)
                {
                    TCPsocket.close();
                    Log.d("TCP Client", "C: socket closed (finally)");
                }
                mMessageListener.messageReceived(Constants.CONNECTION_LOST_MSG);
                mRun = false;
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            mRun = false;
            mMessageListener.messageReceived("error");
        }
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asyncTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}
