package com.actia.mapas.Udp;

import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class UDP_Broadcast extends Thread {
    private static final int TIMEOUT_MS = 10000;

    private boolean shouldContinueSocketListen = true;
    private int iPort;

    private WifiManager mWifi;
    private UDP_BroadcastListener mListener;
    private boolean mInfiniteMode = false;

    private Runnable mRunnable;

    public UDP_Broadcast(WifiManager wifi, Integer port) {
        mWifi = wifi;
        iPort = port;
        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    listenForResponses();

                } catch (IOException e) {
                    e.printStackTrace();
                    com.actia.mapas.Utils.Utils.log("UDP", "Could not send discovery request");
                    if (mListener!=null){
                        mListener.onReceiveTimeout();
                    }
                }
            }
        };
    }

    public interface UDP_BroadcastListener{
        void onResponseReceive(String mMessage);
        void onReceiveTimeout();
    }

    public void setListener(UDP_BroadcastListener listener) {
        this.mListener = listener;
    }

    @Override
	public void run() {
        if (mRunnable!=null){
            setPriority(Thread.MAX_PRIORITY);
            mRunnable.run();
        }
    }

    public synchronized void stopBroadcast(){
        try {
            shouldContinueSocketListen = false;
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start(boolean infiniteMode){
        this.mInfiniteMode = infiniteMode;
        this.start();
    }

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     *
     * @throws IOException
     */
    private void listenForResponses() throws IOException {

        WifiManager.MulticastLock ml = null;

        byte[] buf = new byte[65000];
        DatagramSocket socket;
        socket = new DatagramSocket(null);
        socket.setReceiveBufferSize(512*1024);
        socket.setReuseAddress(true);
        socket.setBroadcast(true);
        socket.setSoTimeout(TIMEOUT_MS);
        socket.bind(new InetSocketAddress(iPort));

        while (shouldContinueSocketListen) {        
	        try {
	            ml = mWifi.createMulticastLock("UDP Broadcasting");
	            ml.acquire();
	
	            System.currentTimeMillis();
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	
	            String message = new String(packet.getData(), "UTF-8").trim();
	//                Utils.log("UDP", "Got UDB broadcast from " + senderIP + ", message: " + message);

                //noinspection ConstantConditions
                if (message != null && !message.equals("")){
                    com.actia.mapas.Utils.Utils.log("UDP", "Received message");
                    if (mListener != null && shouldContinueSocketListen){
                        mListener.onResponseReceive(message);
                    }

                    if (!mInfiniteMode){
                        shouldContinueSocketListen = false;
                    }
                }
	            ml.release();
	            ml = null;
	            buf = new byte[65000];
	        } catch (SocketTimeoutException e) {
                com.actia.mapas.Utils.Utils.log("UDP", "Receive timed out mapas");
	            if (mListener!=null && shouldContinueSocketListen){
	                mListener.onReceiveTimeout();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            if (mListener!=null && shouldContinueSocketListen){
	                mListener.onReceiveTimeout();
	            }
	        }
        }
        if (ml != null){
            try {
                ml.release();
            }catch (RuntimeException ex){
                ex.printStackTrace();
            }
        }
        socket.close();
        socket.disconnect();
    }
}
