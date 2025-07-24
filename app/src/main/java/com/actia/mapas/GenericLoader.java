package com.actia.mapas;

import android.content.Context;
import android.database.Cursor;

import androidx.loader.content.AsyncTaskLoader;

import com.actia.mapas.Utils.Utils;

public abstract class GenericLoader<T> extends AsyncTaskLoader<T> {
	private T mData;
	protected final Context mContext;

	public GenericLoader(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onStartLoading() {
		// Utils.log("Loader", "Starting loader " + this);
		if (mData != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mData);
		} else {
			if (verifyLoad()) {
				forceLoad();
			}
		}
	}

	@Override
	protected T onLoadInBackground() {
		T res = null;
		try {
			long t_start = System.currentTimeMillis();
			res = super.onLoadInBackground();
			long t_gap = System.currentTimeMillis() - t_start;
			Utils.log("Loader", this + " completed in " + (t_gap / 1000) + "s " + (t_gap % 1000) + "ms");
		} catch (Exception e) {
			Utils.log("Loader", "exception caught in " + this + ": " + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
		}
		return res;
	}

	@Override
	protected void onReset() {
		super.onReset();
		if (mData != null) {
			// If we currently have a result available, deliver it
			// immediately.
			onReleaseResources(mData);
			mData = null;
		}
	}

	/**
	 * Called when there is new data to deliver to the client. The super class will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(T res) {
		// Utils.log("Loader", this + " delivering " + res);
		T oldData = mData;
		mData = res;
		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(res);
		}

		// release old resources if not used
		if (oldData != res && oldData != null) {
			onReleaseResources(oldData);
		}
	}

	private void onReleaseResources(T res) {
		// TODO check
		if (res != null && res instanceof Cursor) {
			if (!((Cursor) res).isClosed()){
				((Cursor) res).close();
			}
		}
	}

	protected abstract boolean verifyLoad();
}
