package com.actia.utilities.utilities_internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilitiesNetwork {
    public static int NETWORKCONNECT = 0;


    public static String getTypeConnect(Context context) {
        String typeConnect = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                typeConnect = "wifi";
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                typeConnect = "mobile";
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            typeConnect = "noconnected";
            // not connected to the internet
        }
        return typeConnect;
    }


    public static String getMs(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "ping -c 1 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            String[] op = new String[64];
            String[] delay = new String[8];
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();
            op = output.toString().split("\n");
            delay = op[1].split("time=");

            // body.append(output.toString()+"\n");
            str = delay[1];
            Log.i("Pinger", "Ping: " + delay[1]);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }


    //Verifica si hay salida de internet
    public static boolean isOnline() {
        Process p1 = null;

        boolean reachable = false;
        int returnVal = -1;

        try {
            Log.i("isOnline", "ping -c 1 www.google.com");
            p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            returnVal = p1.waitFor();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (returnVal == NETWORKCONNECT) {
            Log.i("isOnline", "www.google.com reachable");
            reachable = true;
        } else {
            Log.i("isOnline", "www.google.com not reachable");
            reachable = false;
        }
        return reachable;
    }

}
