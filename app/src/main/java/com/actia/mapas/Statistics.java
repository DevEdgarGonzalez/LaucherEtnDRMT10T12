package com.actia.mapas;

import android.content.Context;
import android.net.Uri;

import com.actia.mapas.Utils.Utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Statistics extends Thread {
    private String URL_OS_STATS = "/app/os_stats.php";
    private String URL_VIDEO_STATS = "/app/video_stats.php";
    private String URL_VIDEO_EVENTS = "/app/video_event.php";

    private String[] sArgs;
    private Context mContext;

    public Statistics(Context context, String... args) {
        sArgs = args;
        mContext = context;
    }

    @SuppressWarnings("IfCanBeSwitch")
    @Override
	public void run() {
        String url = null;
        HttpURLConnection urlConnection = null;
        //String resp = null;

        for (int i = 1; i < sArgs.length; ++i) {
        	sArgs[i] = Uri.encode(sArgs[i]);
        }
        if (sArgs != null) {
            try {
                // args[0] => Type of statistic (os_stats, video_stats, video_events)
                if (sArgs[0].equals("os_stats")) {
                    // args[1] => id
                    // args[2] => os
                    // args[3] => os_version
                    // args[4] => app_version
                    // args[5] => app_status
                    URL_OS_STATS += "?id=" + sArgs[1]+ "&os=" + sArgs[2] + "&os_version=" + sArgs[3] + "&app_version=" + sArgs[4] + "&app_status=" + sArgs[5];
                    url = Utils.getServer(Constant.PREFS_SERVER_ADDRESS, mContext) + URL_OS_STATS;

                } else if (sArgs[0].equals("video_stats")) {
                    // args[1] => id
                    // args[2] => file
                    // args[3] => underflow
                    // args[4] => quality
                    URL_VIDEO_STATS += "?id=" + sArgs[1]+ "&file=" + sArgs[2] + "&underflow=" + sArgs[3] + "&quality=" + sArgs[4];
                    url = Utils.getServer(Constant.PREFS_SERVER_ADDRESS, mContext) + URL_VIDEO_STATS;

                } else if (sArgs[0].equals("video_events")) {
                    // args[1] => id
                    // args[2] => file
                    // args[3] => event
                    // args[4] => position
                    URL_VIDEO_EVENTS += "?id=" + sArgs[1]+ "&file=" + sArgs[2] + "&event=" + sArgs[3] + "&position=" + sArgs[4];
                    url = Utils.getServer(Constant.PREFS_SERVER_ADDRESS, mContext) + URL_VIDEO_EVENTS;
                }

                urlConnection = Utils.openConnection(url, mContext);

                @SuppressWarnings("unused") InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                String resp = Utils.convertStreamToString(in);
//                resp.length();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }
}
