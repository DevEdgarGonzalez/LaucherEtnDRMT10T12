package com.actia.encuesta;

import android.util.Log;

import com.actia.home_categories.MainActivity;
import com.actia.infraestructure.ConfigMasterMGC;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();
    private static int serverResponseCode;


    public QueryUtils() {
    }

    /**
     * Return a list of {@link InfoEncuesta} objects.
     */
    public static InfoEncuesta fetchEncuestaData(String requestUrl) throws IOException, XmlPullParserException {
        InfoEncuesta encuestas = null;
        InfoEncuesta xmlRespons = null;

        if (MainActivity.isStp100) {
            URL url = createUrl(requestUrl);

            try {
                xmlRespons = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTPS request: ", e);
            }

        } else {
            try {
                xmlRespons = xmlFileToInfo(ConfigMasterMGC.getConfigSingleton().getXML_ENCUESTA_FILE());
            } catch (IOException | XmlPullParserException ex) {
                Log.e(TAG, "fetchEncuestaData: ", ex);

            }
        }
        encuestas = xmlRespons;

        // Return the list of {@link Earthquake}s
        return encuestas;
    }

    private static InfoEncuesta xmlFileToInfo(String xmlEncuestaFile) throws IOException, XmlPullParserException {

        InfoEncuesta xmlFile = null;
        InputStream inputStream = null;
        String xml = null;
        xml = readXmlFile(xmlEncuestaFile);
        try {
            inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

            xmlFile = new XmlParser().parse(inputStream);
        } catch (Exception ex) {
            Log.e(TAG, "makeXmlFile: ", ex);
            xmlFile = new InfoEncuesta("",0,"",0,new ArrayList<PreguntasEncuesta>());
        }
        return xmlFile;
    }

    private static String readXmlFile(String xmlEncuestaFile) {

        StringBuilder text = new StringBuilder();
        if (new File(xmlEncuestaFile).exists()) {
            try (BufferedReader archivo = new BufferedReader(new FileReader(xmlEncuestaFile))) {

                String line1;

                while ((line1 = archivo.readLine()) != null) {
                    text.append(line1);
                }


            } catch (Exception ex) {
                Log.e(TAG, "readXmlFile: ", ex);
                return null;
            }
        } else {
            return null;
        }
        return text.toString().trim();
    }

    /**
     * Returns URL object from the given String URL
     */

    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a string as te response.
     *
     * @return
     */

    private static InfoEncuesta makeHttpRequest(URL url) throws IOException {
        InfoEncuesta xmlResponse = null;

        // If url is null, then return early
        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                String xmlRead = "";
                inputStream = urlConnection.getInputStream();
                xmlRead = readFromStream(inputStream);
                guardaXmlEncuesta(xmlRead);
                InputStream xmlStream = new ByteArrayInputStream(xmlRead.getBytes());
                xmlResponse = new XmlParser().parse(xmlStream);

            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "Problem retrieving the encuesta XML result ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return xmlResponse;
    }

    public static Integer uploadFile(String sourceFileUri, String to) {


        String fileName = sourceFileUri;
        String cadenaBorrada = sourceFileUri.substring(sourceFileUri.lastIndexOf("_"), sourceFileUri.indexOf("."));
        fileName = fileName.replace(cadenaBorrada, "");

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist : " + sourceFile.getPath());

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(to);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("fichero_archivo", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"fichero_archivo\";filename="
                        + fileName + "" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    Log.e(TAG, "uploadFile: File Upload Complete.");
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                Log.e(TAG, "uploadFile: MalformedURLException Exception : check script url.");

                Log.e(TAG, "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("Upload file Exception", "Exception : "
                        + e.getMessage(), e);
                return 0;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return serverResponseCode;

        } // End else block
    }

    private static void guardaXmlEncuesta(String xmlServidor) throws IOException {
        FileOutputStream stream = null;
        try {

            String path = ConfigMasterMGC.getConfigSingleton().getXML_ENCUESTA_DIR();
            String fileName = "survey.xml";

            File encuesta = new File(path);
            if (!encuesta.isDirectory())
                encuesta.mkdir();

            File archivoEncuesta = new File(path, fileName);

            if (archivoEncuesta.exists()) {
                fileName = "survey_new.xml";
                archivoEncuesta = new File(path, fileName);
            }

            try {
                FileWriter writer = new FileWriter(archivoEncuesta);
                writer.append(xmlServidor);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (archivoEncuesta.exists()) {
                try (BufferedReader archivo = new BufferedReader(new FileReader(path + "survey.xml"));
                     BufferedReader archivoNuevo = new BufferedReader(new FileReader(path + "survey_new.xml"))) {

                    String line1;
                    String line2;
                    boolean areEqual = true;

                    StringBuilder text1 = new StringBuilder();
                    StringBuilder text2 = new StringBuilder();

                    while ((line1 = archivo.readLine()) != null) {
                        text1.append(line1);
                    }

                    while ((line2 = archivoNuevo.readLine()) != null) {
                        text2.append(line2);
                    }

                    if (!text1.toString().equalsIgnoreCase(text2.toString())) {
                        areEqual = false;
                    }

                    if (areEqual) {
                        fileName = "survey_new.xml";
                        archivoEncuesta = new File(path, fileName);
                        archivoEncuesta.delete();
                    } else {
                        fileName = "survey.xml";
                        archivoEncuesta = new File(path, fileName);
                        archivoEncuesta.delete();

                        File from = new File(path, "survey_new.xml");
                        from.renameTo(archivoEncuesta);
                    }

                }
            }

        } catch (IOException e) {

            Log.e(TAG, "guardaXmlEncuesta: ", e);

        } finally {
            if (stream != null)
                stream.close();
        }


    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole xml response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    public static Integer filesToUpload(String mSourceFile, String mUrl, boolean checkStore) {

        int svrCode = 0;
        boolean isUploadCorrect = false;

        try{
            //verifica si coneccion valida a internet

                if (checkStore) {
                    File filesList = new File(ConfigMasterMGC.getConfigSingleton().getENCUESTAS_DIR());
                    if (filesList.listFiles().length > 0) {
                        for (File filesToUpload : filesList.listFiles()) {
                            svrCode = uploadFile(filesToUpload.getPath(), mUrl);
                            if (svrCode == 200) {
                                isUploadCorrect = true;

                                filesToUpload.delete();
                            }
                        }
                    }
                    svrCode = isUploadCorrect ? 200 : 0;
                } else {
                    svrCode = uploadFile(mSourceFile, mUrl);
                }
        }catch (Exception ex){

            Log.e(TAG, "filesToUpload: ", ex);
        }

        return svrCode;
    }
}
