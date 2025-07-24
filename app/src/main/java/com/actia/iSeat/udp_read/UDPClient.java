package com.actia.iSeat.udp_read;

import java.net.InetAddress;

/**
 * Created by Gerardo on 20/11/2018.
 */

public class UDPClient {

    private AutoReceiveMessages fAutoReceiveMessagesThread;
    private static final String TAG = UDPClient.class.getName();

    int fRemoteHostPort;
    byte[] fByteBuffer;
    OnUDPListener fListener;
    public InetAddress fRemoteHostIP;

    int fTimeOut;

    @SuppressWarnings("UnusedParameters")
    public UDPClient(String name, OnUDPListener _lis, byte[] _buffer, int remoteHostPort, int timeOut){
        super();
        fRemoteHostPort = remoteHostPort;
        fByteBuffer = _buffer;
        fListener = _lis;
        fTimeOut = timeOut;
    }

    /**
     * Constructor de la clase UDPClient que servira para la parte ethernet y la comunicacion con el TGU.
     *
     * @param name
     * @param _lis           Listener de una interface UDP
     * @param _buffer        tamaño del buffer a enviar, representado en bytes
     * @param remoteHostIP   Direccion IP remota con la que se comunicara
     * @param remoteHostPort Puerto remoto donde se comunicara
     * @param timeOut        Tiempo limite para la comunacion
     */

    public UDPClient(String name, OnUDPListener _lis, byte[] _buffer, InetAddress remoteHostIP, int remoteHostPort, int timeOut){
        super();
        fRemoteHostIP = remoteHostIP;
        fRemoteHostPort = remoteHostPort;
        fByteBuffer = _buffer;
        fListener = _lis;
        fTimeOut = timeOut;

    }

    public void StartReceivingMessages(UDPClient udpClient){
        StopReceivingMessages();
        fAutoReceiveMessagesThread = new AutoReceiveMessages(udpClient);
        fAutoReceiveMessagesThread.start();
        System.out.println(TAG + "|StartReceivingMessages " + "started");
    }

    public void StopReceivingMessages(){
        if (fAutoReceiveMessagesThread != null){
            fAutoReceiveMessagesThread.Dispose();
            fAutoReceiveMessagesThread.interrupt();
            System.out.println(TAG + "|StopReceivingMessages " + "stopped");
        }
    }
}
