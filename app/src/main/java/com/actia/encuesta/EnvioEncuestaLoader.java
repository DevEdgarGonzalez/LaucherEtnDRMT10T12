package com.actia.encuesta;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class EnvioEncuestaLoader extends AsyncTaskLoader<Integer> {


    private String mUrl;
    private String mSourceFile;
    private boolean mCheckStore;
    private Context mContext;

    /**
     *
     * @param context
     */
    public EnvioEncuestaLoader(@NonNull Context context, String url, String sourceFile, boolean checkStore) {
        super(context);
        this.mUrl = url;
        this.mSourceFile = sourceFile;
        this.mCheckStore = checkStore;
        this.mContext = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public Integer loadInBackground() {
        if(mUrl == null && TextUtils.isEmpty(mSourceFile)){
            return 0;
        }
        Integer result = QueryUtils.filesToUpload( mSourceFile, mUrl, mCheckStore);
        return result;
    }
}
