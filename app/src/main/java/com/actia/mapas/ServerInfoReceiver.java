package com.actia.mapas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.actia.mapas.Udp.UDPServerHandler;
import com.actia.mapas.Udp.UDP_Broadcast;
import com.actia.mapas.Utils.Utils;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ServerInfoReceiver {

	public interface IServerInfoReceiverListener {
		void OnServerInfoReceived();
	}

	private final List<IServerInfoReceiverListener> listeners = new ArrayList<>();
	@SuppressLint("StaticFieldLeak")
	private static ServerInfoReceiver instance = null;
	private Context context = null;

	@SuppressWarnings("FieldCanBeLocal")
	private UDP_Broadcast.UDP_BroadcastListener mUDP_BroadcastListener = new UDP_Broadcast.UDP_BroadcastListener() {
        @SuppressWarnings("WhileLoopReplaceableByForEach")
		@Override
        public void onResponseReceive(String mMessage) {
            UDPServerHandler mHandler;
            SAXParserFactory spf = SAXParserFactory.newInstance();

            try  {
                InputStream inputStream = new ByteArrayInputStream(mMessage.getBytes("UTF-8"));
                InputSource isrc = new InputSource(inputStream);
                isrc.setEncoding("UTF-8");

                SAXParser sp = spf.newSAXParser();

                XMLReader xr = sp.getXMLReader();
                mHandler = new UDPServerHandler(context);
                xr.setContentHandler(mHandler);
                xr.parse(isrc);
                inputStream.close();
                synchronized (listeners) {
	                Iterator<IServerInfoReceiverListener> iter = listeners.iterator();
	                while (iter.hasNext()) {
	                	iter.next().OnServerInfoReceived();
	                }
                }
                int pause = Integer.parseInt(Utils.getServer(Constant.PREFS_PAUSE, context));
                if (pause == 1) {
                	//ToastMessage.get().showToastMessage(R.string.driver_msg);
                	if(Map_Activity.isMessage("driver_msg")){
        	        	String text = Map_Activity.getMessage("start_app_dialog_text");
        	        	Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        	        }
//        	        else{
//        	        	String text = MainActivity.getMessage("start_app_dialog_text");
//        	        }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onReceiveTimeout() {

        }
    };

	private ServerInfoReceiver(int port, Context context) {
		this.context = context;
		UDP_Broadcast udpBroadcast = new UDP_Broadcast((WifiManager) context.getSystemService(Context.WIFI_SERVICE), port);
	    udpBroadcast.setListener(mUDP_BroadcastListener);
	    udpBroadcast.start(true); //infinite loop
	}
	
	public void AddListener(IServerInfoReceiverListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}
	
	public void RemoveListener(IServerInfoReceiverListener listener) {
		synchronized (listeners) {
			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
		}
	}
	
	public static void init(int port, Context context) {	
		if (instance == null) {
			instance = new ServerInfoReceiver(port, context);
		}		
	}
	
	public static ServerInfoReceiver get() {
		return instance;		
	}
}
