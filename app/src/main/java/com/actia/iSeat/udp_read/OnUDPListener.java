package com.actia.iSeat.udp_read;

import java.net.DatagramPacket;

/**
 * Created by Gerardo on 05/12/2017.
 */

public interface OnUDPListener {

    @SuppressWarnings("unused")
    void onRequestListener(boolean valid, String message);
    void onResponseListener(DatagramPacket packet);

}
