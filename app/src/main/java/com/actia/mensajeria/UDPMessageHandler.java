package com.actia.mensajeria;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.helpers.DefaultHandler;

public class UDPMessageHandler extends DefaultHandler {

    private static final String JSON_HEADER_MESSAGE = "";
    private static final String JSON_MESSAGE_ID = "id";
    private static final String JSON_MESSAGE_TEXT = "mensaje";
    private static final String JSON_TIPO = "tipo";
    private static final String JSON_FECHA = "Fecha";
    private static final String JSON_HORA = "Hora";
    private static final String TAG = "UDPMessageHandler";

    private final Info_Message mInfoMessage = new Info_Message();

    public UDPMessageHandler() {

    }

    public Info_Message getInfo() {
        return mInfoMessage;
    }

    public void setJsonInfoMessage(String jsonInfoMessage) throws JSONException {

        try {

            JSONObject rootMessage = new JSONObject(jsonInfoMessage);

            if (rootMessage.has(JSON_MESSAGE_ID))
                mInfoMessage.setIdMessage(rootMessage.getString(JSON_MESSAGE_ID));
            if (rootMessage.has(JSON_MESSAGE_TEXT))
                mInfoMessage.setMessageText(rootMessage.getString(JSON_MESSAGE_TEXT));
            if (rootMessage.has(JSON_TIPO))
                mInfoMessage.setTipo(rootMessage.getString(JSON_TIPO));
            if (rootMessage.has(JSON_FECHA))
                mInfoMessage.setFecha(rootMessage.getString(JSON_FECHA));
            if (rootMessage.has(JSON_HORA))
                mInfoMessage.setHora(rootMessage.getString(JSON_HORA));

        } catch (JSONException JsonEx) {
            Log.e(TAG, "setJsonRoute: ", JsonEx);
        }
    }

}
