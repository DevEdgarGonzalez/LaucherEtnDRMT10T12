package com.actia.mapas;

import android.content.Context;
import android.net.Uri;

import com.actia.mapas.Utils.Utils;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

public class MyUrlConnectionDownloader extends UrlConnectionDownloader {
    private Context context;

    public MyUrlConnectionDownloader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected HttpURLConnection openConnection(Uri path) throws IOException {
        return Utils.openConnection(path.toString(), context);
    }
}
