package com.actia.mapas;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.views.MapView;

///**
// * Created by rubenrodriguezalguacil on 26/09/13.
// */
public class ActiaMapView extends MapView {
    protected ActiaMapView(Context context, int tileSizePixels, ResourceProxy resourceProxy, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs) {
        super(context, tileSizePixels, resourceProxy, tileProvider, tileRequestCompleteHandler, attrs);
    }

    public ActiaMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActiaMapView(Context context, int tileSizePixels) {
        super(context, tileSizePixels);
    }

    public ActiaMapView(Context context, int tileSizePixels, ResourceProxy resourceProxy) {
        super(context, tileSizePixels, resourceProxy);
    }

    public ActiaMapView(Context context, int tileSizePixels, ResourceProxy resourceProxy, MapTileProviderBase aTileProvider) {
        super(context, tileSizePixels, resourceProxy, aTileProvider);
    }

    public ActiaMapView(Context context, int tileSizePixels, ResourceProxy resourceProxy, MapTileProviderBase aTileProvider, Handler tileRequestCompleteHandler) {
        super(context, tileSizePixels, resourceProxy, aTileProvider, tileRequestCompleteHandler);
    }

    @Override
    public int getMaxZoomLevel() {
        return 14;
    }

    @Override
    public int getMinZoomLevel() {
        return 0;
    }
}
