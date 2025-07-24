package com.actia.iSeat;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Edgar Gonzalez on 27/11/2018.
 * Actia de Mexico S.A. de C.V..
 */
public class Seat {
    @SerializedName("number")
    private int number;

    @SerializedName("passenger")
    private boolean passenger;

    @SerializedName("seatbelt")
    private boolean seatBealt;


    public Seat() {
    }

    public Seat(int number, boolean passenger, boolean seatBealt) {
        this.number = number;
        this.passenger = passenger;
        this.seatBealt = seatBealt;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isPassenger() {
        return passenger;
    }

    public void setPassenger(boolean passenger) {
        this.passenger = passenger;
    }

    public boolean isSeatBealt() {
        return seatBealt;
    }

    public void setSeatBealt(boolean seatBealt) {
        this.seatBealt = seatBealt;
    }
}
