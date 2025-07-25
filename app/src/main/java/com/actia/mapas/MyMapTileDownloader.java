package com.actia.mapas;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.actia.mapas.Utils.Utils;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileRequestState;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase.LowMemoryException;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

public class MyMapTileDownloader extends MapTileModuleProviderBase {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final Logger logger = LoggerFactory.getLogger(MyMapTileDownloader.class);

    // ===========================================================
    // Fields
    // ===========================================================

    private final IFilesystemCache mFilesystemCache;

    private OnlineTileSourceBase mTileSource;

    private final INetworkAvailablityCheck mNetworkAvailablityCheck;

    private Context mContext;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MyMapTileDownloader(final ITileSource pTileSource,
                               final IFilesystemCache pFilesystemCache,
                               final INetworkAvailablityCheck pNetworkAvailablityCheck,
                               Context context) {
        this(pTileSource, pFilesystemCache, pNetworkAvailablityCheck,
                NUMBER_OF_TILE_DOWNLOAD_THREADS, TILE_DOWNLOAD_MAXIMUM_QUEUE_SIZE, context);

    }

    public MyMapTileDownloader(final ITileSource pTileSource,
                               final IFilesystemCache pFilesystemCache,
                               final INetworkAvailablityCheck pNetworkAvailablityCheck, int pThreadPoolSize,
                               int pPendingQueueSize, Context context) {
        super(pThreadPoolSize, pPendingQueueSize);

        mFilesystemCache = pFilesystemCache;
        mNetworkAvailablityCheck = pNetworkAvailablityCheck;
        mContext = context;
        setTileSource(pTileSource);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

//    public ITileSource getTileSource() {
//        return mTileSource;
//    }

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean getUsesDataConnection() {
        return true;
    }

    @Override
    protected String getName() {
        return "Online Tile Download Provider";
    }

    @Override
    protected String getThreadGroupName() {
        return "downloader";
    }

    @Override
    protected Runnable getTileLoader() {
        return new TileLoader();
    }

    @Override
    public int getMinimumZoomLevel() {
        return (mTileSource != null ? mTileSource.getMinimumZoomLevel() : MINIMUM_ZOOMLEVEL);
    }

    @Override
    public int getMaximumZoomLevel() {
        return (mTileSource != null ? mTileSource.getMaximumZoomLevel() : MAXIMUM_ZOOMLEVEL);
    }

    @Override
    public void setTileSource(final ITileSource tileSource) {
        // We are only interested in OnlineTileSourceBase tile sources
        if (tileSource instanceof OnlineTileSourceBase) {
            mTileSource = (OnlineTileSourceBase) tileSource;
        } else {
            // Otherwise shut down the tile downloader
            mTileSource = null;
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class TileLoader extends MapTileModuleProviderBase.TileLoader {

    	public TileLoader(){
    		logger.debug("TileLoader()");
    	}
        @Override
        public Drawable loadTile(final MapTileRequestState aState) throws CantContinueException {

            if (mTileSource == null) {
                return null;
            }

            InputStream in = null;
            OutputStream out = null;
            final MapTile tile = aState.getMapTile();

            try {

                if (mNetworkAvailablityCheck != null
                        && !mNetworkAvailablityCheck.getNetworkAvailable()) {
                    if (DEBUGMODE) {
                        logger.debug("Skipping " + getName() + " due to NetworkAvailabliltyCheck.");
                    }
                    return null;
                }

                final String tileURLString = mTileSource.getTileURLString(tile);
                if (!tileURLString.contains("osm")){
                	logger.debug("osm not find");
                }

                if (DEBUGMODE) {
                    logger.debug("Downloading Maptile from url: " + tileURLString);
                }

                if (TextUtils.isEmpty(tileURLString)) {
                    return null;
                }

                //HttpURLConnection urlConnection = Utils.openHttpsConnection(tileURLString, mContext);
                logger.debug("tileURLString " + tileURLString);
                HttpURLConnection urlConnection = Utils.openConnection(tileURLString, mContext);
                
                //HttpURLConnection urlConnection = (HttpURLConnection) new URL(tileURLString).openConnection(); 
                
                /*
                urlConnection.setUseCaches(true);
                urlConnection.setRequestProperty(OpenStreetMapTileProviderConstants.USER_AGENT, OpenStreetMapTileProviderConstants.getUserAgentValue());
                urlConnection.connect();
                */

                // Check to see if we got success

                if (urlConnection.getResponseCode() != 200) {
                     logger.error("Problem downloading MapTile: " + tileURLString + " HTTP response: " + urlConnection.getResponseMessage());
                    return null;
                }

                //final HttpEntity entity = response.getEntity();
                if (urlConnection.getInputStream() == null) {
                    logger.warn("No content downloading MapTile: " + tile);
                    return null;
                }
                //in = entity.getContent();
                in = new BufferedInputStream(urlConnection.getInputStream());

                final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
                StreamUtils.copy(in, out);
                out.flush();
                final byte[] data = dataStream.toByteArray();
                final ByteArrayInputStream byteStream = new ByteArrayInputStream(data);

                // Save the data to the filesystem cache
                
                if (mFilesystemCache != null) {
                    mFilesystemCache.saveFile(mTileSource, tile, byteStream);
                    byteStream.reset();
                }

                return mTileSource.getDrawable(byteStream);
            } catch (final UnknownHostException e) {
                // no network connection so empty the queue
                logger.warn("UnknownHostException downloading MapTile: " + tile + " : " + e);
                throw new CantContinueException(e);
            } catch (final LowMemoryException e) {
                // low memory so empty the queue
                logger.warn("LowMemoryException downloading MapTile: " + tile + " : " + e);
                throw new CantContinueException(e);
            } catch (final FileNotFoundException e) {
                logger.warn("Tile not found: " + tile + " : " + e);
            } catch (final IOException e) {
                logger.warn("IOException downloading MapTile: " + tile + " : " + e);
            } catch (final Throwable e) {
                logger.error("Error downloading MapTile: " + tile, e);
            } finally {
                StreamUtils.closeStream(in);
                StreamUtils.closeStream(out);
            }

            return null;
        }

        @Override
        protected void tileLoaded(final MapTileRequestState pState, final Drawable pDrawable) {
            //removeTileFromQueues(pState.getMapTile());
            // don't return the tile because we'll wait for the fs provider to ask for it
            // this prevent flickering when a load of delayed downloads complete for tiles
            // that we might not even be interested in any more
            pState.getCallback().mapTileRequestCompleted(pState, null);
        }

    }
}
