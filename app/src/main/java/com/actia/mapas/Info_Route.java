package com.actia.mapas;

import android.util.Log;

import java.util.ArrayList;

public class Info_Route {
    private String gpx;
    private String first;
    private String last;
    private String next;
    private Integer next_dist;
    private Double remain_dist;
    private Double total_dist;
    private ArrayList<POI> POIs;
    private Integer gps;
    private Integer altitude;
    private Double latitude;
    private Double longitude;
    private Integer speed;
    private String id;

    String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    String getGpx() {
        return gpx;
    }

    public void setGpx(String gpx) {
        this.gpx = gpx;
        Log.e("TAG", "setGpx: " + this.gpx );
    }

    String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    @SuppressWarnings("unused")
    public Integer getNext_dist() {
        return next_dist;
    }

    public void setNext_dist(Integer next_dist) {
        this.next_dist = next_dist;
    }

    @SuppressWarnings("unused")
    public Double getRemain_dist() {
        return remain_dist;
    }

    public void setRemain_dist(Double remain_dist) {
        this.remain_dist = remain_dist;
    }

    @SuppressWarnings("unused")
    public Double getTotal_dist() {
        return total_dist;
    }

    public void setTotal_dist(Double total_dist) {
        this.total_dist = total_dist;
    }

    ArrayList<POI> getPOIs() {
        return POIs;
    }

    public void setPOIs(ArrayList<POI> POIs) {
        this.POIs = POIs;
    }

    @SuppressWarnings("unused")
    public Integer getGps() {
        return gps;
    }

    public void setGps(Integer gps) {
        this.gps = gps;
    }

    Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        Log.e("TAG", "setLatitude: "+ this.latitude );
    }

    Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
