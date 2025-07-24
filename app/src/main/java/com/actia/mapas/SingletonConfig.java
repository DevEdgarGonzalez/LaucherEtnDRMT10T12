package com.actia.mapas;


///**
// * Created by Omar Sevilla  on 03/04/2017.
// */

@SuppressWarnings("FieldCanBeLocal")
public class SingletonConfig {

    private static SingletonConfig singleton;
    private final String IP_SERVER_ACTIES = "http://192.168.1.2/";
    private String helperWebServicePHP = "/video/video.php?file=";
    private int countPromosMovies = 0;
    private String ENCUESTA_XML_URL = "survey/survey.xml";
    private String ENCUESTA_STATS_URL = "stats";
    private String ENCUESTA_UPLOAD_URL = "survey/uploadFiles.php";
    private volatile boolean promosPassed = false;
    private volatile String deviceID = null;
    private volatile String busId = null;

    public static SingletonConfig getConfigSingleton(){
        if(singleton == null){
            singleton = new SingletonConfig();
        }
        return singleton;
    }

    @SuppressWarnings("unused")
    public int getCountPromosMovies(){
        return countPromosMovies;
    }

    @SuppressWarnings("unused")
    public void setCountPromosMovie(int countPromosMovies){
        this.countPromosMovies = countPromosMovies;
    }

    @SuppressWarnings("unused")
    public String getIP_SERVER_ACTIES() {
        return IP_SERVER_ACTIES;
    }

    public String getHelperWebServicePHP() {
        return helperWebServicePHP;
    }

    public String getENCUESTA_XML_URL(){return ENCUESTA_XML_URL;}

    public String getENCUESTA_STATS_URL(){return ENCUESTA_STATS_URL;}

    public String getENCUESTA_UPLOAD_URL(){return ENCUESTA_UPLOAD_URL;}

    @SuppressWarnings("unused")
    public void setHelperWebServicePHP(String helperWebServicePHP) {
        this.helperWebServicePHP = helperWebServicePHP;
    }

    public String getDeviceId(){
        return deviceID;
    }

    @SuppressWarnings("unused")
    public void setDeviceId(String deviceID){
        this.deviceID = deviceID;
    }

    public String getBusId(){
        return busId;
    }

    @SuppressWarnings("unused")
    public void setBusId(String deviceID){
        this.busId = deviceID;
    }
}
