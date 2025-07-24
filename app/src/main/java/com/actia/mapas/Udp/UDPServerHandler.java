package com.actia.mapas.Udp;

import android.content.Context;
import android.util.Log;

import com.actia.mapas.Constant;
import com.actia.mapas.Utils.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UDPServerHandler extends DefaultHandler {
	private static final String XML_SERVER = "SERVER";
	private static final String XML_NAME = "name";
	private static final String XML_COSTUMER = "costumer";
	private static final String XML_LINE = "line";
	private static final String XML_ADDRESS = "address";
	private static final String XML_IV = "iv";
	private static final String XML_ID = "id";
	private static final String XML_USER = "user";
	private static final String XML_PASSWORD = "password";
	private static final String XML_BANNER_DELAY = "banner_delay";
	private static final String XML_BANNER_PERIOD = "banner_period";
	private static final String XML_BANNER_TIME = "banner_time";
	private static final String XML_PAUSE = "pause";
	private static final String XML_VERSION = "version";
	private static final String XML_LIVE_VIDEO = "video_live";
	private static final String XML_RADIO = "radio";

	private static final String PROTOCOL = "http://";
	private static final String TAG = "UDPServerHandler";

	private Context mContext;

	public UDPServerHandler(Context context) {
		mContext = context;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		Log.d(TAG, "Server status parse");
		// parse SERVER
		if (localName.equalsIgnoreCase(XML_SERVER) && attributes.getLength() != 0) {
			Utils.setServer(Constant.PREFS_SERVER_NAME, attributes.getValue(XML_NAME), mContext);
			Utils.setServer(Constant.PREFS_SERVER_COSTUMER, attributes.getValue(XML_COSTUMER), mContext);
			Utils.setServer(Constant.PREFS_SERVER_ID, attributes.getValue(XML_ID), mContext);
			Utils.setServer(Constant.PREFS_SERVER_LINE, attributes.getValue(XML_LINE), mContext);
			//noinspection IndexOfReplaceableByContains
			if (attributes.getValue(XML_ADDRESS).indexOf(PROTOCOL) == -1) {
				//Utils.setServer(Constant.PREFS_SERVER_ADDRESS, "https://" + attributes.getValue(XML_ADDRESS), mContext);
				Utils.setServer(Constant.PREFS_SERVER_ADDRESS, "http://" + attributes.getValue(XML_ADDRESS), mContext);
			} else {
				Utils.setServer(Constant.PREFS_SERVER_ADDRESS, attributes.getValue(XML_ADDRESS), mContext);
			}
			Utils.setServer(Constant.PREFS_SERVER_IV, "403aea44d0331493bf478d7fd3281a96", mContext);
			Utils.setServer(Constant.PREFS_SERVER_USER, "admin", mContext);
			Utils.setServer(Constant.PREFS_SERVER_PASSWORD, "admin1234", mContext);
			Utils.setServer(Constant.PREFS_BANNER_DELAY, attributes.getValue(XML_BANNER_DELAY), mContext);
			Utils.setServer(Constant.PREFS_BANNER_PERIOD, attributes.getValue(XML_BANNER_PERIOD), mContext);
			Utils.setServer(Constant.PREFS_BANNER_TIME, attributes.getValue(XML_BANNER_TIME), mContext);
			Utils.setServer(Constant.PREFS_PAUSE, attributes.getValue(XML_PAUSE), mContext);
			try {
				Utils.setServer(Constant.PREFS_VERSION, attributes.getValue(XML_VERSION), mContext);
			} catch (Exception e) {
				Utils.setServer(Constant.PREFS_VERSION, "0", mContext);
			}
			try {
				Utils.setServer(Constant.PREFS_LIVE_VIDEO, attributes.getValue(XML_LIVE_VIDEO), mContext);
			} catch (Exception e) {
				Utils.setServer(Constant.PREFS_LIVE_VIDEO, "0", mContext);
			}
			try {
				Utils.setServer(Constant.PREFS_RADIO, attributes.getValue(XML_RADIO), mContext);
			} catch (Exception e) {
				Utils.setServer(Constant.PREFS_RADIO, "0", mContext);
			}
			Log.d(TAG, "SERVER PAUSE => " + Utils.getServer(Constant.PREFS_PAUSE, mContext));
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
	}

}
