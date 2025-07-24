package com.actia.mapas.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.fragment.app.FragmentActivity;

import com.actia.mapas.AESUtil;
import com.actia.mapas.Base64;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

public class Utils {
    private static final int DEFAULT_READ_TIMEOUT = 20 * 1000; // 20s
    private static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000; // 15s
    private static final String TAG = "Utils";
    private static String httpAuthStr = null;
    @SuppressLint("StaticFieldLeak")
    private static Picasso picasso = null;
    private static boolean newKey = true;

    private static final boolean DEBUG = true;

    public static HttpURLConnection openConnection(String path, Context context) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
        //connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes(HttpAuthorization(context).getBytes()));
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        return connection;
    }

    public static HttpURLConnection openHttpConnection(String path, Context context) throws IOException {
    	
      //trustAllHosts();
      HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
      Log.e("openHttpConnection", "openConnection");
      //connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes(HttpAuthorization(context).getBytes()));
      connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
      connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
      return connection;
  }

    static String HttpAuthorization(Context context) {
        //noinspection StringEquality
        if (httpAuthStr == null || httpAuthStr == "" || newKey)
        {
        	Log.d(TAG, "HttpAuthorization: NEW KEY!");
            String password = AESUtil.decrypt(
                    getServer(com.actia.mapas.Constant.PREFS_SERVER_PASSWORD, context),
                    new SecretKeySpec(Utils.GenerateMasterKey(), "AES"),
                    Utils.HexStringTobyteArray(getServer(com.actia.mapas.Constant.PREFS_SERVER_IV, context)));
            httpAuthStr = getServer(com.actia.mapas.Constant.PREFS_SERVER_USER, context) + ":" + password;
            Log.e(TAG, "HttpAuthorization: httpAuthStr: " + httpAuthStr );
            newKey = false;
        }
        return httpAuthStr;
    }

    public static String AuthInUrl(String sUrl, Context context) {
        int iAuthPosition = sUrl.indexOf("://");
        if (iAuthPosition != -1) {
            String shema = sUrl.substring(0, iAuthPosition + 3);
            String path = sUrl.substring(iAuthPosition + 3);
            sUrl = shema + Utils.HttpAuthorization(context) + "@" + path;
        }
        return sUrl;
    }

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader is = new InputStreamReader(inputStream, "UTF-8");
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }

        br.close();
        is.close();
        inputStream.close();
        return sb.toString();
    }

    public static int convertDPtoPX(Resources resources, int iDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, iDp, resources.getDisplayMetrics());
    }

    @SuppressWarnings("deprecation")
    public static void showProgressBar(FragmentActivity activity, boolean show) {
        if (activity != null) {
            if (!show) {
                activity.setProgressBarIndeterminateVisibility(false);
            } else {
                activity.setProgressBarIndeterminateVisibility(true);
            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Calculates the angle from centerPt to targetPt in degrees.
     * The return should range from [0,360), rotating CLOCKWISE,
     * 0 and 360 degrees represents NORTH,
     * 90 degrees represents EAST, etc...
     * <p/>
     * Assumes all points are in the same coordinate space.  If they are not,
     * you will need to call SwingUtilities.convertPointToScreen or equivalent
     * on all arguments before passing them  to this function.
     *
     * @param centerPt Point we are rotating around.
     * @param targetPt Point we want to calcuate the angle to.
     * @return angle in degrees.  This is the angle from centerPt to targetPt.
     */
    public static double calcRotationAngleInDegrees(Point centerPt, Point targetPt) {
        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(targetPt.y - centerPt.y, targetPt.x - centerPt.x);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        theta += Math.PI / 2.0;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public static String getVersionName(Context context) {
        String versionName = "";

        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static byte[] GenerateMasterKey() {
        final byte KEY_1 = 20;
        final byte KEY_2 = 21;
        final byte KEY_3 = 22;
        final byte KEY_4 = 30;
        final byte KEY_5 = 31;
        final byte KEY_6 = 32;

        MessageDigest sha256 = null;

        try {
            sha256 = MessageDigest.getInstance("SHA-256");

            byte c = KEY_1;
            sha256.update(c);
            c = (byte) (c * 2);
            sha256.update(c);
            c = (byte) (c - c / 2);
            sha256.update(c);
            c++;
            sha256.update(c);
            c = (byte) (c + c / 5);
            sha256.update(c);
            c = (byte) (c - 9);
            sha256.update(c);

            byte c2 = KEY_2;
            sha256.update(c2);
            c2 += 45;
            sha256.update(c2);
            if (c2 > KEY_6) {
                c2 -= c / 5 - 1;
                sha256.update(c2);
            }
            c2 += c / KEY_3;
            sha256.update(c2);

            c = (byte) (c2 + 3);
            sha256.update(c);
            c = (byte) (c - c / 2);
            sha256.update(c);
            c++;
            sha256.update(c);
            c = (byte) (c + c / 5);
            sha256.update(c);
            c += KEY_4;
            sha256.update(c);

            c2 -= 15;
            sha256.update(c2);
            if (c2 > KEY_6) {
                c2 += c / 5;
                sha256.update(c2);
            }
            c2 += c / KEY_5;
            sha256.update(c2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //return byteArrayToHexString(sha256.digest());

        //noinspection ConstantConditions
        return sha256.digest();
    }

//    public static String byteArrayToHexString(byte[] array) {
//        StringBuffer hexString = new StringBuffer();
//        for (byte b : array) {
//            int intVal = b & 0xff;
//            if (intVal < 0x10)
//                hexString.append("0");
//            hexString.append(Integer.toHexString(intVal));
//        }
//        return hexString.toString();
//    }

    public static byte[] HexStringTobyteArray(String src) {
        byte[] iv = new byte[src.length() / 2];
        int k = 0;

        for (int i = 0; i + 1 < src.length(); i += 2, k++) {
            iv[k] = (byte) (Character.digit(src.charAt(i), 16) << 4);
            iv[k] += (byte) (Character.digit(src.charAt(i + 1), 16));
        }

        return iv;
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("CommitPrefEdits")
    public static void setServer(String sOption, String sValue, Context context) {
        SharedPreferences settings = context.getSharedPreferences(com.actia.mapas.Constant.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();

        if (!getServer(sOption, context).equals(sValue))
        {
            //noinspection IfCanBeSwitch
            if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_NAME)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_NAME, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_ID)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_ID, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_COSTUMER)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_COSTUMER, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_LINE)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_LINE, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_ADDRESS)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_ADDRESS, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_IV)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_IV, sValue);
	            newKey = true;
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_USER)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_USER, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_PASSWORD)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_SERVER_PASSWORD, sValue);
	            newKey = true;
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_BANNER_DELAY)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_BANNER_DELAY, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_BANNER_PERIOD)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_BANNER_PERIOD, sValue);
	        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_BANNER_TIME)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_BANNER_TIME, sValue);
	        }else if (sOption.equals(com.actia.mapas.Constant.PREFS_PAUSE)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_PAUSE, sValue);
	        }else if (sOption.equals(com.actia.mapas.Constant.PREFS_VERSION)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_VERSION, sValue);
	        }else if (sOption.equals(com.actia.mapas.Constant.PREFS_LIVE_VIDEO)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_LIVE_VIDEO, sValue);
	        }else if (sOption.equals(com.actia.mapas.Constant.PREFS_RADIO)) {
	            prefEditor.putString(com.actia.mapas.Constant.PREFS_RADIO, sValue);
	        }
	        prefEditor.commit();
        }
    }

    public static String getServer(String sOption, Context context) {
        SharedPreferences settings = context.getSharedPreferences(com.actia.mapas.Constant.PREFS_NAME, Context.MODE_PRIVATE);

        //noinspection IfCanBeSwitch
        if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_NAME)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_NAME, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_ID)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_ID, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_COSTUMER)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_COSTUMER, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_LINE)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_LINE, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_ADDRESS)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_ADDRESS, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_IV)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_IV, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_USER)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_USER, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_SERVER_PASSWORD)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_SERVER_PASSWORD, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_BANNER_TIME)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_BANNER_TIME, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_BANNER_PERIOD)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_BANNER_PERIOD, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_BANNER_DELAY)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_BANNER_DELAY, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_PAUSE)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_PAUSE, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_VERSION)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_VERSION, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_LIVE_VIDEO)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_LIVE_VIDEO, "");
        } else if (sOption.equals(com.actia.mapas.Constant.PREFS_RADIO)) {
            return settings.getString(com.actia.mapas.Constant.PREFS_RADIO, "");
        }

        return null;
    }

    public static void log(String tag, String text){
        if (DEBUG){
            Log.d(tag, text);
        }
    }

    public static String fetchUrlAsString(String urlString) throws IOException {
        return readStream(fetchUrl(urlString));
    }
    private static InputStream fetchUrl(String urlString) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            return new FlushedInputStream(conn);
        } catch (IOException e) {
            if (conn != null) {
                conn.disconnect();
            }
            throw e;
        }
    }

    private static String readStream(InputStream in) throws IOException {
        InputStreamReader is = new InputStreamReader(in, "UTF-8");
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }

        br.close();
        is.close();
        in.close();
        return sb.toString();
    }

    private static class FlushedInputStream extends FilterInputStream {

        HttpURLConnection mConnection;

        public FlushedInputStream(HttpURLConnection connection) throws IOException {
            super(null); // D:
            try {
                mConnection = connection;
//				mConnection.setRequestProperty("User-Agent","<em>MY USER AGENT</em>");
//                mConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
//                mConnection.setRequestProperty("Accept-Encoding", "identity");
                mConnection.connect();
                this.in = connection.getInputStream();
            } catch (IOException ioe) {
                try {
                    connection.disconnect();
                } catch (Exception ee) {
                    // silence
                }
                try {
                    if (this.in != null) {
                        this.in.close();
                    }
                } catch (IOException ioe2) {
                    // silence!
                }
                throw ioe;
            }
        }

        public FlushedInputStream(InputStream inputStream) throws IOException {
            super(inputStream);
            if (inputStream == null) {
                throw new IOException();
            }
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (mConnection != null) {
                mConnection.disconnect();
            }
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int _byte = read();
                    if (_byte < 0) {
                        break; // we reached EOF
                    }
                    bytesSkipped = 1; // we read one byte
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {

        if (data==null || data.length<=0){
            return null;
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap decodeSampledBitmapFromUrl(String url, int reqWidth, int reqHeight) {

        if (TextUtils.isEmpty(url)){
            return null;
        }

        InputStream is = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            is = Utils.fetchUrl(url);
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            is = Utils.fetchUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is, null, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        if (res==null){
            return null;
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getWindowHeight(Activity context){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.heightPixels;
    }

    public static int getActinBarHeight(Context context){
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

	public static String getAuth(Context context) {
		return "Basic " + Base64.encodeBytes(HttpAuthorization(context).getBytes());
	}
}
