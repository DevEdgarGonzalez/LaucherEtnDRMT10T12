package com.actia.mensajeria;

import static com.actia.home_categories.MainActivity.restartBroadcast;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actia.encuesta.EncuestaActivity;
import com.actia.mapas.Map_Activity;
import com.actia.mexico.launcher_t12_generico_2018.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UDP_Broadcast extends Thread {
    private static final int TIMEOUT_MS = 10000;

    private boolean shouldContinueSocketListen = true;
    private final int iPort;

    private final WifiManager mWifi;
    private UDP_BroadcastListener mListener;
    public static Context mContext;
    private boolean mInfiniteMode = false;

    public static String mIdMessageGeneral = "";
    public static String mIdMessageGeocerca = "";
    public static String mIdMessageEncuesta = "";
    public static Info_Message infoMessage;
    public static final int ROUTE_MSG_ID = 9876;

    private final Runnable mRunnable;

    public UDP_Broadcast(WifiManager wifi, Integer port) {
        mWifi = wifi;
        iPort = port;
        mRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    listenForResponses();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    Log.e("UDP", "Could not send discovery request");
                    if (mListener != null) {
                        mListener.onReceiveTimeout();
                    }
                }
            }
        };
    }

    public interface UDP_BroadcastListener {
        void onResponseReceive(String mMessage);

        void onReceiveTimeout();
    }

    public void setListener(UDP_BroadcastListener listener, Context context) {
        this.mListener = listener;
        mContext = context;
    }

    @Override
    public void run() {
        if (mRunnable != null) {
            setPriority(Thread.MAX_PRIORITY);
            mRunnable.run();
        }
    }

    public synchronized void stopBroadcast() {
        try {
            shouldContinueSocketListen = false;
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start(boolean infiniteMode) {
        this.mInfiniteMode = infiniteMode;
        this.start();
    }

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     *
     * @throws IOException
     */
    private void listenForResponses() throws IOException, InterruptedException {

        //WifiManager.MulticastLock ml = null;

        byte[] buf = new byte[1024];
        DatagramSocket socket;
        socket = new DatagramSocket(null);
        socket.setReceiveBufferSize(512 * 1024);
        socket.setReuseAddress(true);
        socket.setBroadcast(true);
        socket.setSoTimeout(TIMEOUT_MS);
        socket.bind(new InetSocketAddress(iPort));

        while (shouldContinueSocketListen) {
            try {
                System.currentTimeMillis();
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String message = new String(packet.getData(), StandardCharsets.UTF_8).trim();

                if (message != null && !message.equals("")) {
                    Log.i("UDP", "Received message");
                    if (mListener != null && shouldContinueSocketListen) {
                        mListener.onResponseReceive(message);
                    }

                    if (!mInfiniteMode) {
                        shouldContinueSocketListen = false;
                    }
                }
                buf = new byte[1024];
            } catch (SocketTimeoutException e) {
                Log.e("UDP", "Receive timed out");
                if (mListener != null && shouldContinueSocketListen) {
                    buf = new byte[1024];
                    /*Thread.sleep(TIMEOUT_MS);
                    mListener.onReceiveTimeout();*/
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mListener != null && shouldContinueSocketListen) {
                    buf = new byte[1024];
                    mListener.onReceiveTimeout();
                }
            }
        }
        socket.close();
        socket.disconnect();
    }

    public static UDP_BroadcastListener mUDP_BroadcastListener = new UDP_BroadcastListener() {
        @Override
        public void onResponseReceive(String mMessage) {
            UDPMessageHandler mHandler;

            try {
                mHandler = new UDPMessageHandler();

                mHandler.setJsonInfoMessage(mMessage);
                infoMessage = mHandler.getInfo();

                Message mRouteMessage = new Message();
                mRouteMessage.what = ROUTE_MSG_ID;
                mRouteHandler.sendMessage(mRouteMessage);
            } catch (Exception e) {
                Log.e("TAG", "onResponseReceive: ", e);
            }
        }

        @Override
        public void onReceiveTimeout() {
            if (this != null) {
                restartBroadcast();
            }
        }
    };


    @SuppressWarnings("HandlerLeak")
    public static Handler mRouteHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ROUTE_MSG_ID:

                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.message_layout, null);
                    final Dialog alertDialog = new Dialog(mContext);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(view);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView message_image_view = view.findViewById(R.id.message_image_view);
                    TextView text_view_message = view.findViewById(R.id.text_view_message);
                    TextView text_view_date = view.findViewById(R.id.text_view_date);
                    TextView text_view_time = view.findViewById(R.id.text_view_time);
                    try{

                        Button buttonOK = view.findViewById(R.id.btnOk);
                        Button buttonMap = view.findViewById(R.id.btnMapa);
                        ImageButton imageButtonClose = view.findViewById(R.id.btnClose);


                        Log.i("mensaje", "infoMessage.getTipo(): " + infoMessage.getTipo());
                        if (infoMessage.getTipo().trim().equalsIgnoreCase("geocerca")) {

                            mIdMessageGeocerca = readLastIdMessage("InfoMensajeIdGeo").trim();
                            Log.i("mensaje", "mIdMessageGeocerca: " + mIdMessageGeocerca);

                            if (!infoMessage.getIdMessage().isEmpty() && infoMessage.getIdMessage() != null) {
                                if (Integer.parseInt(infoMessage.getIdMessage().trim()) != Integer.parseInt(mIdMessageGeocerca)) {
                                    saveLastIdMessage("InfoMensajeIdGeo", infoMessage.getIdMessage());

                                    if(mContext.getClass().getName() == Map_Activity.class.getName())
                                        buttonMap.setVisibility(View.GONE);
                                    else
                                        buttonMap.setVisibility(View.VISIBLE);

                                    message_image_view.setImageResource(R.drawable.geo_message);
                                    text_view_message.setText(Html.fromHtml(infoMessage.getMessageText()));
                                    //text_view_title.setText("Aviso:");
                                    text_view_date.setText(infoMessage.getFecha());
                                    text_view_time.setText(infoMessage.getHora());

                                    imageButtonClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    buttonMap.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(mContext, Map_Activity.class);
                                            mContext.startActivity(intent);
                                            alertDialog.dismiss();
                                        }
                                    });

                                    buttonOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                    Log.i("mensaje", "handleMessage: im alive id: " + infoMessage.getIdMessage() + " mensaje: " + infoMessage.getMessageText());

                                }
                            }
                        } else if (infoMessage.getTipo().trim().equalsIgnoreCase("general")){
                            mIdMessageGeneral = readLastIdMessage("InfoMensajeIdGeneral").trim();
                            Log.i("mensaje", "mIdMessageGeneral: " + mIdMessageGeneral);

                            if (!infoMessage.getIdMessage().isEmpty() && infoMessage.getIdMessage() != null) {
                                if (Integer.parseInt(infoMessage.getIdMessage().trim()) != Integer.parseInt(mIdMessageGeneral)) {
                                    //mIdMessage = infoMessage.getIdMessage();
                                    saveLastIdMessage("InfoMensajeIdGeneral", infoMessage.getIdMessage());
                                    buttonMap.setVisibility(View.GONE);

                                    message_image_view.setImageResource(R.drawable.message);
                                    text_view_message.setText(Html.fromHtml(infoMessage.getMessageText()));
                                    //text_view_title.setText("Aviso:");
                                    text_view_date.setText(infoMessage.getFecha());
                                    text_view_time.setText(infoMessage.getHora());

                                    buttonOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    imageButtonClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    alertDialog.show();
                                    Log.i("mensaje", "handleMessage: im alive id: " + infoMessage.getIdMessage() + " mensaje: " + infoMessage.getMessageText());
                                }
                            }
                        } else {

                            mIdMessageEncuesta = readLastIdMessage("InfoMensajeIdEncuesta").trim();
                            Log.i("mensaje", "mIdMessageEncuesta: " + mIdMessageEncuesta);

                            if (!infoMessage.getIdMessage().isEmpty() && infoMessage.getIdMessage() != null) {
                                if (Integer.parseInt(infoMessage.getIdMessage().trim()) != Integer.parseInt(mIdMessageEncuesta)) {
                                    //mIdMessage = infoMessage.getIdMessage();
                                    saveLastIdMessage("InfoMensajeIdEncuesta", infoMessage.getIdMessage());

                                    Intent intentone = new Intent(mContext.getApplicationContext(), EncuestaActivity.class);
                                    //intentone.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intentone);
                                }
                            }
                        }
                    } catch (Exception ex){
                        Log.e("Mensajeria", "handleMessage: ", ex);
                    }
                    break;
            }
        }
    };

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(String dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    static void saveLastIdMessage(String nombre, String idMessage) {
        Log.e("UDP Broadcast", "saveLastIdMessage: " + idMessage);
        SharedPreferences sharedPref = mContext.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lastIdMessage", idMessage);
        editor.apply();
    }

    static String readLastIdMessage(String nombre) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        Log.e("UDP Broadcast", "readLastIdMessage: " + sharedPref.getString("lastIdMessage", "0"));
        return sharedPref.getString("lastIdMessage", "0");
    }
}
