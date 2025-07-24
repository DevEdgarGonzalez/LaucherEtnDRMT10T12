package com.actia.mapas.Udp;

import android.content.Context;
import android.util.Log;

import com.actia.mapas.Info_RouteJson;
import com.actia.mexico.launcher_t12_generico_2018.R;

import org.json.JSONException;
import org.json.JSONObject;

public class UDPRouteHandleJson {

    private static final String JSON_ROUTE_DATE = "Fecha";
    private static final String JSON_ROUTE_HOUR = "Hora";
    private static final String JSON_ROUTE_LATITUDE = "Latitud";
    private static final String JSON_ROUTE_LONGITUDE = "Longitud";
    private static final String JSON_ROUTE_SPEED = "Velocidad";
    private static final String JSON_ROUTE = "Rumbo";
    private static final String JSON_ROUTE_GPS_OK = "GpsValido";
    private static final String TAG = "UDPRouteHandleJson";


    private Info_RouteJson irInfo = new Info_RouteJson();
    private Context mContext;
    //private ArrayList<POI> POIs;

    public UDPRouteHandleJson(Context context) {
        this.mContext = context;
    }

    public Info_RouteJson getInfo() {
        return irInfo;
    }

    public void setJsonRoute(String jsonRoute) throws JSONException {

        try {

            JSONObject root = new JSONObject(jsonRoute);

            if(root.has(JSON_ROUTE_DATE)){
                irInfo.setFecha(root.getString(JSON_ROUTE_DATE));
            }
            if(root.has(JSON_ROUTE_HOUR)){
                irInfo.setHora(root.getString(JSON_ROUTE_HOUR));
            }
            if(root.has(JSON_ROUTE_LATITUDE)){
                irInfo.setLatitud(root.getDouble(JSON_ROUTE_LATITUDE));
            }
            if(root.has(JSON_ROUTE_LONGITUDE)){
                irInfo.setLongitud(root.getDouble(JSON_ROUTE_LONGITUDE));
            }
            if(root.has(JSON_ROUTE_SPEED)){
                irInfo.setVelocidad(root.getDouble(JSON_ROUTE_SPEED));
            }
            if(root.has(JSON_ROUTE) && root.getInt(JSON_ROUTE) > -1){
                String cadenaRumbo = "";
                if (root.getInt(JSON_ROUTE) == 0 || root.getInt(JSON_ROUTE) == 360)
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.north_string);

                else if(root.getInt(JSON_ROUTE) > 0 && root.getInt(JSON_ROUTE) < 90)
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.northeast_string);

                else if(root.getInt(JSON_ROUTE) == 90)
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.east_string);

                else if(root.getInt(JSON_ROUTE) > 90 && root.getInt(JSON_ROUTE) < 180)
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.southeast_string);

                else if(root.getInt(JSON_ROUTE) == 180)
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.south_string);

                else if(root.getInt(JSON_ROUTE) > 180 && root.getInt(JSON_ROUTE) < 270)
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.southwest_string);

                else if(root.getInt(JSON_ROUTE) == 270)
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.west_string);

                else
                    cadenaRumbo = root.getInt(JSON_ROUTE) + "° " + mContext.getResources().getString(R.string.northwest_string);

                irInfo.setRumbo(cadenaRumbo);
            }
            if(root.has(JSON_ROUTE_GPS_OK)){
                irInfo.setGpsOk(root.getBoolean(JSON_ROUTE_GPS_OK));
            }

        }catch (JSONException JsonEx){
            Log.e(TAG, "setJsonRoute: ", JsonEx );
        }
    }

}
