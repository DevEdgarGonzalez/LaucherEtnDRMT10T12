package com.actia.iSeat.udp_read;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Gerardo on 20/11/2018.
 */

 class UDPManager {
    @SuppressWarnings("WeakerAccess")
    protected static OnUDPListener Lis = null;

    @SuppressWarnings("WeakerAccess")
    public static String receiveRequest(int port, byte[] buf, int timeout, OnUDPListener _lis){
        String result = null;
        DatagramSocket socket = null;
        Lis = _lis;

        try{
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(timeout);
            socket.receive(packet);
            if (Lis != null){
                Lis.onResponseListener(packet);
            }
            result = new String(packet.getData(), 0, packet.getLength());
            result.length();
        }catch (UnknownHostException e){
//            LogFile.createLogFile("" + e, "UDPManager receiveRequest " + " - " + e, UtilitiesDate.getCurrentDate(), Calendar.getInstance());
            e.printStackTrace();
        }catch (SocketTimeoutException e) {
            //LogFile.createLogFile("" + e, "" + "Socket " + " - " + e, UtilitiesDate.getCurrentDate(), Calendar.getInstance());
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (socket != null){
                socket.close();
            }
        }

        return result;
    }
}
