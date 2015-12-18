package com.paulina.sadowska.robotwirelesscontroller.Wifi;

import android.os.AsyncTask;

import com.paulina.sadowska.robotwirelesscontroller.MessageManager;

/**
 * Created by palka on 18.12.15.
 */
public class WifiMessagesManager {

    private TCPClient mTcpClient;
    private MessageManager messageManager;
    private String serverIP;
    private int port;

    public void connect(){
        new connectTask().execute("");
    }

    public WifiMessagesManager(String serverIP, int port)
    {
        this.serverIP = serverIP;
        this.port = port;
        messageManager = new MessageManager();
    }

    public boolean getConnectionState() {
        if(mTcpClient != null)
            return mTcpClient.getConnectionState();
        else
            return false;
    }

    public void disconnect(){
        if(mTcpClient!=null)
            mTcpClient.stopClient();
    }

    public void sendMessage(String textToSend){
        //sends the message to the server
        if (mTcpClient != null) {
            mTcpClient.sendMessage(textToSend);
        }
    }

    private class connectTask extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {


            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //publish message
                    publishProgress(message);
                }
            }, serverIP, port);
            mTcpClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0] != null) {
                //display message
                messageManager.WriteReceivedMessageToInBuffer(values[0]);
            }
        }
    }


}
