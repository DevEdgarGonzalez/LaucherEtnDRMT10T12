package com.actia.iSeat.udp_read;

import android.util.Log;

/**
 * Created by Gerardo on 20/11/2018.
 */

class AutoReceiveMessages extends Thread {

    private final UDPClient fMyClient;
    private boolean fActive = true;

    AutoReceiveMessages(UDPClient myClient){
        fMyClient = myClient;
    }

    @Override
    public void run(){
        while (fActive){
            try{
                String received = UDPManager.receiveRequest(fMyClient.fRemoteHostPort, fMyClient.fByteBuffer, fMyClient.fTimeOut, fMyClient.fListener);
                if (received != null){
                    Log.d("RECEIVED: ", "" + received);
                }


                
                
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    void Dispose(){
        fActive = false;
    }
}
