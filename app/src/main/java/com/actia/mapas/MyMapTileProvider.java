package com.actia.mapas;

import android.content.Context;

import org.osmdroid.tileprovider.IMapTileProviderCallback;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

public class MyMapTileProvider extends MapTileProviderArray implements IMapTileProviderCallback {
    public MyMapTileProvider(final Context pContext, final ITileSource pTileSource) {
        this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext),
                pTileSource, pContext);
    }

    public MyMapTileProvider(final IRegisterReceiver pRegisterReceiver,
                                final INetworkAvailablityCheck aNetworkAvailablityCheck, final ITileSource pTileSource, final Context pContext) {
        super(pTileSource, pRegisterReceiver);

        final TileWriter tileWriter = new TileWriter();

        
        final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
                pRegisterReceiver, pTileSource);
        mTileProviderList.add(fileSystemProvider);
        
        final MapTileFileArchiveProvider archiveProvider = new MapTileFileArchiveProvider(
                pRegisterReceiver, pTileSource);
        mTileProviderList.add(archiveProvider);
        
        final MyMapTileDownloader downloaderProvider = new MyMapTileDownloader(pTileSource, tileWriter,
                aNetworkAvailablityCheck, pContext);
        mTileProviderList.add(downloaderProvider);
    }
}
