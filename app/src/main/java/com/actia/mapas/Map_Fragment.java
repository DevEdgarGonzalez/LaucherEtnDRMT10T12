package com.actia.mapas;

import static com.actia.mapas.Map_Activity.isAnimated;
import static com.actia.mapas.Map_Activity.mCourseTextView;
import static com.actia.mapas.Map_Activity.mSpeedTextView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.actia.mapas.Udp.UDPRouteHandleJson;
import com.actia.mapas.Udp.UDP_Broadcast;
import com.actia.mapas.Utils.Utils;
import com.actia.mexico.launcher_t12_generico_2018.R;
import com.fmt.gps.track.TrackPoint;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.List;

//TODO: Error OutOfMemoryError loading bitmap (osmdroid tileprovider tilesource BitmapTileSourceBase)
public class Map_Fragment extends Fragment {

    private final static int ROUTE_MSG_ID = 9876;
    private static final int GPX_LOADER = 2003;
    private static final String GPX_BUNDLE_PARAM = "URL";
    private static final String TAG = "Map_Fragment";
    protected ActiaMapView mMapView;
    protected ResourceProxy mResourceProxy;

    private static final String URL_TILES = "http://192.168.1.2/tiles/";

    protected PathOverlay roadOverlay;
    private Info_RouteJson irRoute = new Info_RouteJson();

    private UDP_Broadcast udpBroadcast;

    private Map_Fragment_Listener mListener;

    private WifiManager wifi;

    private boolean centerBusPosition = true;

    public interface Map_Fragment_Listener {
        void onInfoListener(Info_RouteJson mInfo);

        void onChangeFollowingStatus(boolean isFollowing);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mResourceProxy = new DefaultResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new ActiaMapView(inflater.getContext(), 512, mResourceProxy);


        // Custom Tile Server
        String[] addr = new String[1];
        addr[0] = URL_TILES;
        Log.e("Mapa", "onCreateView: addr " + addr[0]);
        final ITileSource tileSource = new XYTileSource("ACTIA", null, 0, 14, 256, ".png", addr);

        final MyMapTileProvider tileProvider = new MyMapTileProvider(inflater.getContext().getApplicationContext(),
                tileSource);
        final TilesOverlay tilesOverlay = new TilesOverlay(tileProvider, inflater.getContext().getApplicationContext());
        mMapView.getOverlays().add(tilesOverlay);
        //float density = getResources().getDisplayMetrics().density * 2.0f;
        //TileSystem.setTileSize((int) (tileSource.getTileSizePixels() * density));
        return mMapView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Utils.showProgressBar(getActivity(), true);
        wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setOnTouchListener(onMapTouchListener);
        mMapView.getController().setZoom(13);

    }

    private View.OnTouchListener onMapTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // stopBroadcast();
            setCenterBusPosition(false);
            Map_Activity.mIvCentrar.setImageResource(R.drawable.centrar_gps_off);
            if(!isAnimated){
                Map_Activity.mIvCentrar.startAnimation(Map_Activity.animation);
                isAnimated = true;
            }

            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        restartBroadcast();
    }

    @Override
    public void onPause() {
        stopBroadcast();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopBroadcast();
        mMapView.getTileProvider().clearTileCache();
        mMapView.removeAllViews();
        mMapView = null;
        //System.gc();

    }

    public void setCenterBusPosition(boolean b) {
        centerBusPosition = b;
        if (b) {
            updateBusPosition();
        }
        if (mListener != null) {
            mListener.onChangeFollowingStatus(b);
        }
    }

    public void startBroadcast() {
        Integer iPortRoute = 5001;
        udpBroadcast = new UDP_Broadcast(wifi, iPortRoute);
        udpBroadcast.setListener(mUDP_BroadcastListener);
        udpBroadcast.start(true);

        if (mListener != null) {
            mListener.onChangeFollowingStatus(true);
        }
    }

    public void stopBroadcast() {
        if (udpBroadcast != null && udpBroadcast.isAlive()) {
            udpBroadcast.stopBroadcast();
            if (mListener != null) {
                mListener.onChangeFollowingStatus(false);
            }
        }
    }

    Handler mHandler = new Handler();

    public void restartBroadcast() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopBroadcast();
                startBroadcast();
            }
        });
    }

    UDP_Broadcast.UDP_BroadcastListener mUDP_BroadcastListener = new UDP_Broadcast.UDP_BroadcastListener() {
        @Override
        public void onResponseReceive(String mMessage) {
            UDPRouteHandleJson mHandler;

            try {
                if (mMapView != null) {
                    mHandler = new UDPRouteHandleJson(mMapView.getContext());

                    mHandler.setJsonRoute(mMessage);

                    irRoute = mHandler.getInfo();

                    Message mRouteMessage = new Message();
                    mRouteMessage.what = ROUTE_MSG_ID;
                    mRouteHandler.sendMessage(mRouteMessage);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onReceiveTimeout() {
            /*if (getActivity() != null) {
                restartBroadcast();
            }*/
        }
    };

    @SuppressWarnings("HandlerLeak")
    Handler mRouteHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ROUTE_MSG_ID:
                    Utils.showProgressBar(getActivity(), false);
                    if (mMapView != null) {
                        drawRoute();
                    }
                    if (!loaderRunning && !isRouteRendered) {
                        //Launch route downloader
                        loaderRunning = true;
                        Bundle bundle = new Bundle();
                        bundle.putString(GPX_BUNDLE_PARAM, "");
                        getLoaderManager().restartLoader(GPX_LOADER, bundle, mLoaderCallbacks);
                    }
                    break;
            }
        }
    };

    private boolean isRouteRendered = false;
    private boolean isPoisRendered = false;
    private boolean loaderRunning = false;

    private void drawRoute() {
        if (mListener != null) {
            mListener.onInfoListener(irRoute);
        }

        if (irRoute == null) {
            clearRoute();
            clearPois();
        } else {
            if (roadOverlay != null && !isRouteRendered) {
                mMapView.getOverlays().add(roadOverlay);
                isRouteRendered = true;
            }
            /*if (irRoute.getPOIs() != null && !isPoisRendered) {
                paintPOIs(irRoute.getPOIs());
                isPoisRendered = true;
            } else if (irRoute.getPOIs() == null || irRoute.getPOIs().isEmpty()) {
                clearPois();
            }*/

            Log.e(TAG, "drawRoute: irRoute " + irRoute.getLatitud() + ", " + irRoute.getLongitud() + ", " + irRoute.getGpsOk());
            if (irRoute.getLatitud() != null && irRoute.getLongitud() != null && irRoute.getGpsOk()) {
                paintBUS(irRoute.getLatitud(), irRoute.getLongitud());
            }
            if(irRoute.getRumbo() != null && irRoute.getGpsOk())
                mCourseTextView.setText(irRoute.getRumbo());

            if(irRoute.getVelocidad() != null && irRoute.getGpsOk()){

                mSpeedTextView.setText(irRoute.getVelocidad() + " km/h");

                if(irRoute.getVelocidad() >= 95){
                    mSpeedTextView.setTextColor(getContext().getResources().getColor(R.color.red_nps));
                }
                else{
                    mSpeedTextView.setTextColor(getContext().getResources().getColor(R.color.black));
                }
            }
        }
        updateBusPosition();
    }

    private void updateBusPosition() {
        if (irRoute != null && irRoute.getLatitud() != null && irRoute.getLongitud() != null && irRoute.getGpsOk()) {
            if (centerBusPosition) {
                GeoPoint gp = new GeoPoint(irRoute.getLatitud(), irRoute.getLongitud());
                mMapView.getController().animateTo(gp);
                mMapView.getController().setZoom(13);
            }
            mMapView.postInvalidate();
        }
    }

    private void clearPois() {
        if (poisMarkers != null) {
            poisMarkers.removeAllItems();
        }
        isPoisRendered = false;
    }

    private void clearRoute() {
        if (roadOverlay != null) {
            roadOverlay.clearPath();
        }
        isRouteRendered = false;
    }

    private LoaderManager.LoaderCallbacks<List<TrackPoint>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<TrackPoint>>() {
        @Override
        public Loader<List<TrackPoint>> onCreateLoader(int id, Bundle bundle) {
            switch (id) {
                case GPX_LOADER:
                    return new GpxDownloader(getActivity(), bundle.getString(GPX_BUNDLE_PARAM));
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<TrackPoint>> listLoader, List<TrackPoint> trackPoints) {
            switch (listLoader.getId()) {
                case GPX_LOADER: {
                    if (roadOverlay != null) {
                        roadOverlay.clearPath();
                        isRouteRendered = false;
                    }
                    if (trackPoints != null) {
                        if (roadOverlay == null) {
                            roadOverlay = RoadManager.buildRoadOverlay(null, mMapView.getContext());
                        }
                        for (TrackPoint trackPoint : trackPoints) {
                            roadOverlay.addPoint(new GeoPoint(trackPoint.getLat(), trackPoint.getLon()));
                        }
                    }
                    break;
                }
            }
            loaderRunning = false;
        }

        @Override
        public void onLoaderReset(Loader<List<TrackPoint>> listLoader) {
        }
    };

    ItemizedOverlayWithBubble<ExtendedOverlayItem> poisMarkers = null;

    @SuppressWarnings("deprecation")
    private void paintPOIs(ArrayList<POI> POIs) {
        if (mMapView != null && POIs != null && !POIs.isEmpty()) {
            if (poisMarkers == null) {
                final ArrayList<ExtendedOverlayItem> poiItems = new ArrayList<>();
                poisMarkers = new ItemizedOverlayWithBubble<>(getActivity(), poiItems, mMapView);
                mMapView.getOverlays().add(poisMarkers);
            } else {
                poisMarkers.removeAllItems();
            }

            // POI Items
            mMapView.getOverlays().add(poisMarkers);

            int size = POIs.size();
            for (int i = 0; i < size; i++) {
                POI poi = POIs.get(i);
                ExtendedOverlayItem poiMarker = new ExtendedOverlayItem(poi.getName(), "", new GeoPoint(
                        poi.getLatitude(), poi.getLongitude()), mMapView.getContext());
                Drawable marker;

                if (i == 0) {
                    // TODO: esto deberia hacerce con los puntos de ruta y no
                    // con los POI
                    marker = new BitmapDrawable(getResources(), getMarkerStart(
                            new GeoPoint(poi.getLatitude(), poi.getLongitude()), new GeoPoint(POIs.get(i + 1)
                                    .getLatitude(), POIs.get(i + 1).getLongitude())));
                } else if (i == size - 1) {
                    marker = getResources().getDrawable(R.drawable.marker_end);
                } else {
                    marker = getResources().getDrawable(R.drawable.marker_poi);
                }

                poiMarker.setMarker(marker);
                poiMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
                poisMarkers.addItem(poiMarker);
            }
            // End POI Items


            Log.e(TAG, "paintPOIs: size " + size);
        }
    }

    ItemizedOverlayWithBubble<ExtendedOverlayItem> busMarkers = null;

    @SuppressWarnings("deprecation")
    private void paintBUS(Double latitude, Double longitude) {
        Log.e(TAG, "paintBUS: mMapView " + mMapView);
        if (mMapView != null) {
            if (busMarkers == null) {
                final ArrayList<ExtendedOverlayItem> poiItems = new ArrayList<>();
                Log.e(TAG, "paintBUS: mMapView " + mMapView);
                busMarkers = new ItemizedOverlayWithBubble<>(getActivity(), poiItems, mMapView);
                mMapView.getOverlays().add(busMarkers);
            } else {
                busMarkers.removeAllItems();
            }

            Log.e(TAG, "paintBUS: busMarkers " + busMarkers);
            String routeTitle = Utils.getServer(Constant.PREFS_SERVER_NAME, getActivity().getApplicationContext());
            ExtendedOverlayItem poiMarker = new ExtendedOverlayItem(routeTitle, "", new GeoPoint(latitude, longitude),
                    mMapView.getContext());

            Drawable icon = ContextCompat.getDrawable(getActivity(), R.drawable.marker_pin);
            poiMarker.setMarker(icon);
            poiMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
            busMarkers.addItem(poiMarker);
        }
    }

    private Bitmap getMarkerStart(GeoPoint poi_start, GeoPoint poi_end) {
        Point point_start = new Point(), point_end = new Point();

        org.osmdroid.views.MapView.Projection projection = mMapView.getProjection();
        projection.toPixels(poi_start, point_start);
        projection.toPixels(poi_end, point_end);

        Bitmap bMarkerStart = BitmapFactory.decodeResource(getResources(), R.drawable.marker_start);

        return Utils.RotateBitmap(bMarkerStart, (float) Utils.calcRotationAngleInDegrees(point_start, point_end));
    }
}