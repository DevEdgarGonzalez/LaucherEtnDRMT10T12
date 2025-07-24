package com.actia.mapas;

import android.content.Context;

import com.actia.mapas.Utils.Utils;
import com.fmt.gps.data.GpxFileDataAccess;
import com.fmt.gps.track.TrackPoint;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

public class GpxDownloader extends GenericLoader<List<TrackPoint>> {

	private String sUrl;

	public GpxDownloader(Context context, String url) {
		super(context);
		sUrl = url;
	}

	@Override
	protected boolean verifyLoad() {
		return true;
	}

	public List<TrackPoint> loadInBackground() {
		HttpURLConnection urlConnection = null;
		List<TrackPoint> mData = null;

		try {
			urlConnection = Utils.openConnection(sUrl, mContext);

			InputStream in = new BufferedInputStream(urlConnection.getInputStream());

			mData = GpxFileDataAccess.getPoints(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return mData;
	}

}
