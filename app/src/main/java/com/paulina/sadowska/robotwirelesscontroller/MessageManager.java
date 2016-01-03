package com.paulina.sadowska.robotwirelesscontroller;

/**
 * Created by palka on 18.12.15.
 */
public class MessageManager {


    private char[] inBuffer = new char[Constants.INPUT_MESSAGE_LENGTH];
    private boolean messageInProgress = false;
    private int messageIndex = 0;
    private OnMessageReceived receiveListener;

    public MessageManager(OnMessageReceived listener)
    {
        receiveListener = listener;
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asyncTask doInBackground
    public interface OnMessageReceived {
        void messageReceived();
    }

    //Message in format {AAABBB} where A - alpha, B-velocity
    //or {S} when robot shouldn't move
    public String getMessageText(){
        String message = Constants.START_BYTE_STR;

        if(Utilities.getRobotIsMovingFlag())
            message += Constants.ROBOT_MOVE_STR;
        else
            message += Constants.ROBOT_STOP_STR;


        message += String.format("%03d", Utilities.getAlpha());
        message += Utilities.getVelocityStr();
        message += Constants.STOP_BYTE_STR;
        return message;
    }


    private void readCurrents()
    {
        int current1;
        int current2;
        String temp = inBuffer[2]+""+inBuffer[3]+""+inBuffer[4]+""+inBuffer[5];
        try {
            current1 = Integer.parseInt(temp);
        } catch (NumberFormatException ex) {
            current1 = 0;
        }
        temp = inBuffer[7]+""+inBuffer[8]+""+inBuffer[9]+""+inBuffer[10];
        try {
            current2 = Integer.parseInt(temp);
        } catch (NumberFormatException ex) {
            current2 = 0;
        }

        if(inBuffer[1]==Constants.MINUS_SIGN)
            current1 *= (-1);
        if(inBuffer[6]==Constants.MINUS_SIGN)
            current2 *= (-1);

        Utilities.setCurrentmV(current1, 1);
        Utilities.setCurrentmV(current2, 2);
        receiveListener.messageReceived();
    }

    public void WriteReceivedMessageToInBuffer(String message)
    {
        for(char c: message.toCharArray())
        {
            if(c==Constants.START_BYTE)
            {
                messageIndex = 0;
                messageInProgress = true;
                inBuffer[messageIndex] = c;
            }
            else if(c==Constants.STOP_BYTE && messageInProgress)
            {
                messageIndex++;
                messageInProgress = false;
                inBuffer[messageIndex] = c;
                if(messageIndex==Constants.INPUT_MESSAGE_LENGTH-1)
                {
                    readCurrents();
                }
                /*else if (messageIndex == Constants.INPUT_CONNECTION_MESSAGE_LENGTH- 1)
                {
                    if(inBuffer[Constants.INDEX_CONNECTION_INFO]==Constants.CONNECTION_ACK_BYTE)
                    {
                        Utilities.setConnectionState(true);
                        //start sending data, init sending thread
                        controlMessageThread.post(sendControlMessage);
                    }
                }*/
            }
            else if(messageInProgress)
            {
                messageIndex++;
                inBuffer[messageIndex] = c;
            }
        }
    }

}
