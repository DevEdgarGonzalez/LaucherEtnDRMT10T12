package com.actia.iSeat.udp_read;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;
import android.util.Log;

import com.actia.iSeat.DataSensor;
import com.actia.iSeat.Seat;
import com.actia.infraestructure.ConstantsApp;
import com.actia.infraestructure.ContextApp;
import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by Gerardo on 20/11/2018.
 */

public class ReadUDPDataService extends IntentService implements OnUDPListener {

    private UDPClient udpClient;
    private static final int count = 0;
    private Handler handler;
    private Runnable runnable;
    private long distance;
    private ResultReceiver receiver;
    private ResultReceiver receiver2;
    Bundle bundle;
    Bundle fullBundle;
    Gson gson;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    public ReadUDPDataService(){
        super(ReadUDPDataService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent){
        receiver = intent.getParcelableExtra("receiver");
        bundle = new Bundle();
        // Objeto para el manejo de las direcciones ip
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(ConstantsApp.IP_RASP);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }

        int PORT = ConstantsApp.PORT_RASP;
        udpClient = new UDPClient("UDPEthernet", ReadUDPDataService.this, new byte[1000], inetAddress, PORT, 10*1000);
        udpClient.StartReceivingMessages(udpClient);
    }
    @Override
    public void onRequestListener(boolean valid, String message) {

    }

    @Override
    public void onResponseListener(DatagramPacket packet){
        String aux = null;
        //            aux = "";
        aux = new String(packet.getData(), 0, packet.getLength());
//            aux = new String(packet.getData(), "UTF-8");

        if (aux != null){
            // parsear el string al formato JSON
            Log.d("JSON",aux.trim());
            updateImages(aux.trim());
        }
    }

    public void updateImages(String geojson) {
        try {
            gson = new Gson();

            if (geojson != null && !geojson.equals("")) {
                Gson gson = new Gson();
                DataSensor dataSensor = gson.fromJson(geojson, DataSensor.class);



                //validateBus
                if (dataSensor.getBus() == null || !dataSensor.getBus().contains(String.valueOf(ContextApp.idBuss))) {
                    return;
                }

                //validate Seat
                if (dataSensor != null && dataSensor.getAsientos() != null && dataSensor.getAsientos().length > 0) {

                    int idSeat = 0;

                    try {
                        idSeat = Integer.parseInt(ContextApp.idSeat);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    for (Seat seat : dataSensor.getAsientos()) {
                        if (seat.getNumber() == idSeat) {
                            Intent intentDataSensor = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("passenger", seat.isPassenger());
                            bundle.putBoolean("seatbelt", seat.isSeatBealt());
                            intentDataSensor.putExtras(bundle);
                            intentDataSensor.setAction("allDataSensor");
                            getApplicationContext().sendBroadcast(intentDataSensor);
                            Log.d("ReadUDP", "Send Broadcast dataSeat auxJson: " + "   " + seat.isPassenger() + "   " + seat.isSeatBealt());
                            break;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            // If an error occurs loading in the GeoJSON file or adding the points to the list,
            // we log the error.
            Log.e("ReadUDPDataService", "Exception Loading GeoJSON: " + exception.toString());
        }
    }
}
