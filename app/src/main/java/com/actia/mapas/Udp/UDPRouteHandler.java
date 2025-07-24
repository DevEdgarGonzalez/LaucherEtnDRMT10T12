package com.actia.mapas.Udp;

import android.content.Context;

import com.actia.mapas.Constant;
import com.actia.mapas.Info_Route;
import com.actia.mapas.POI;
import com.actia.mapas.Utils.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class UDPRouteHandler extends DefaultHandler {
    private static final String XML_ROUTE = "ROUTE_INFO";
    private static final String XML_ROUTE_ID = "id";
    private static final String XML_ROUTE_GPX= "gpx";
//    private static final String XML_ROUTE_FIRST = "first";
    private static final String XML_ROUTE_FIRST_ID = "first_id";
//    private static final String XML_ROUTE_LAST = "last";
    private static final String XML_ROUTE_LAST_ID = "last_id";
//    private static final String XML_ROUTE_NEXT = "next";
    private static final String XML_ROUTE_NEXT_ID = "next_id";
    private static final String XML_ROUTE_NEXT_DIST = "next_dist";
    private static final String XML_ROUTE_REMAIN_DIST = "remain_dist";
    private static final String XML_ROUTE_TOTAL_DIST = "total_dist";

    private static final String XML_GPS = "GPS_INFO";
    private static final String XML_GPS_WORKING = "gps_ok";
    private static final String XML_GPS_ALTITUDE = "alt";
    private static final String XML_GPS_LATITUDE = "lat";
    private static final String XML_GPS_LONGITUDE = "long";
    private static final String XML_GPS_SPEED = "speed";

    private static final String XML_POI = "POI";
    private static final String XML_POI_IDX = "idx";
    private static final String XML_POI_LAT = "lat";
    private static final String XML_POI_LONG = "long";
    private static final String XML_POI_NAME = "name";
    private static final String XML_POI_TYPE = "type";
    private static final String XML_POI_TIME = "time";

    private Info_Route irInfo;
    private ArrayList<POI> POIs;

    private Context mContext;

    public UDPRouteHandler(Context context) {
        mContext = context;
    }

//    public ArrayList<POI> getPOIs() {
//        return POIs;
//    }

    public Info_Route getInfo() {
        return irInfo;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();

        irInfo = new Info_Route();
        POIs = new ArrayList<>();

        /*irInfo.setLatitude(19.511255);
        irInfo.setLongitude(-99.150656);*/
    }

    @Override
    public void startElement(String uri, String localName,
                             String name, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);

        try{

        // parse ROUTE
        if (localName.equalsIgnoreCase(XML_ROUTE) && attributes.getLength() != 0) {
            if (attributes.getIndex(XML_ROUTE_FIRST_ID) > -1) {
                try{
                    irInfo.setFirst(attributes.getValue(XML_ROUTE_FIRST_ID));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_ROUTE_GPX) > -1) {
                try{
                    irInfo.setGpx(Utils.getServer(Constant.PREFS_SERVER_ADDRESS, mContext) + attributes.getValue(XML_ROUTE_GPX));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_ROUTE_LAST_ID) > -1) {
                try{
                    irInfo.setLast(attributes.getValue(XML_ROUTE_LAST_ID));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_ROUTE_NEXT_ID) > -1) {
                try{
                    irInfo.setNext(attributes.getValue(XML_ROUTE_NEXT_ID));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_ROUTE_NEXT_DIST) > -1) {
                try{
                    irInfo.setNext_dist(Integer.valueOf(attributes.getValue(XML_ROUTE_NEXT_DIST)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_ROUTE_REMAIN_DIST) > -1) {
                try{
                    irInfo.setRemain_dist(Double.valueOf(attributes.getValue(XML_ROUTE_REMAIN_DIST)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_ROUTE_TOTAL_DIST) > -1) {
                try{
                    irInfo.setTotal_dist(Double.valueOf(attributes.getValue(XML_ROUTE_TOTAL_DIST)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_ROUTE_ID) > -1) {
                try{
                    irInfo.setId(attributes.getValue(XML_ROUTE_ID));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        // parse GPS
        if (localName.equalsIgnoreCase(XML_GPS) && attributes.getLength() != 0) {
            if (attributes.getIndex(XML_GPS_WORKING) > -1) {
                try{
                    irInfo.setGps(Integer.valueOf(attributes.getValue(XML_GPS_WORKING)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_GPS_ALTITUDE) > -1) {
                try{
                    irInfo.setAltitude(Integer.valueOf(attributes.getValue(XML_GPS_ALTITUDE)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_GPS_LATITUDE) > -1) {
                try{
                    irInfo.setLatitude(Double.valueOf(attributes.getValue(XML_GPS_LATITUDE)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_GPS_LONGITUDE) > -1) {
                try{
                    irInfo.setLongitude(Double.valueOf(attributes.getValue(XML_GPS_LONGITUDE)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_GPS_SPEED) > -1) {
                try{
                    irInfo.setSpeed(Integer.valueOf(attributes.getValue(XML_GPS_SPEED)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        // parse POI
        if (localName.equalsIgnoreCase(XML_POI) && attributes.getLength() != 0) {
            POI POIActual = new POI();

            if (attributes.getIndex(XML_POI_IDX) > -1) {
                try{
                    POIActual.setIdx(attributes.getValue(XML_POI_IDX));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_POI_LAT) > -1) {
                try{
                    POIActual.setLatitude(Double.valueOf(attributes.getValue(XML_POI_LAT)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_POI_LONG) > -1) {
                try{
                    POIActual.setLongitude(Double.valueOf(attributes.getValue(XML_POI_LONG)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_POI_NAME) > -1) {
                try{
                    POIActual.setName(attributes.getValue(XML_POI_NAME));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_POI_TYPE) > -1) {
                try{
                    POIActual.setType(Integer.valueOf(attributes.getValue(XML_POI_TYPE)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (attributes.getIndex(XML_POI_TIME) > -1) {
                try{
                    POIActual.setTime(attributes.getValue(XML_POI_TIME));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            POIs.add(POIActual);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);

        // parse POI
        if (localName.equalsIgnoreCase(XML_ROUTE)) {
            irInfo.setPOIs(POIs);
        }
    }

}
