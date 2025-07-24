package com.actia.utilities.utilities_date;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class UtilsDate {
    public static String getDatecomplete(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateFom= dateFormat.format(cal.getTime());
        return dateFom;
    }

    public static String  getStringDateFromTimestamp(long dateTimestamp){
        Calendar c =  Calendar.getInstance();
        c.setTimeInMillis(dateTimestamp);
        c.get(Calendar.MINUTE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateFom= dateFormat.format(c.getTime());

        return dateFom;



    }


}
