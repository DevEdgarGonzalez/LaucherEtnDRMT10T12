package com.actia.iSeat;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Edgar Gonzalez on 27/11/2018.
 * Actia de Mexico S.A. de C.V..
 */
public class DataSensor {

    @SerializedName("bus")
    private String bus;
    @SerializedName("asientos")
    private Seat[] asientos;


    public DataSensor() {
    }

    public DataSensor(String bus, Seat[] asientos) {
        this.bus = bus;
        this.asientos = asientos;
    }


    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public Seat[] getAsientos() {
        return asientos;
    }

    public void setAsientos(Seat[] asientos) {
        this.asientos = asientos;
    }
}
