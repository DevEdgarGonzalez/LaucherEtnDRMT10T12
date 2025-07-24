package com.actia.encuesta;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class EncuestaLoader extends AsyncTaskLoader<InfoEncuesta> {

    private String mUrl;
    private Context mContext;

    /**
     *
     * @param context
     */
    public EncuestaLoader(@NonNull Context context, String url) {
        super(context);
        this.mUrl = url;
        this.mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public InfoEncuesta loadInBackground() {
        if(mUrl == null){
            return null;
        }
        InfoEncuesta result = null;
        try {
            result = QueryUtils.fetchEncuestaData(mUrl);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return result;
    }
}
