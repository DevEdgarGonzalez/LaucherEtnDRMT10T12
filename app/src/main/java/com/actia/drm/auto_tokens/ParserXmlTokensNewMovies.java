package com.actia.drm.auto_tokens;

import android.content.Context;
import android.util.Log;

import com.actia.infraestructure.ConfigMasterMGC;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class ParserXmlTokensNewMovies {

    private static final String TAG_XML_FILES = "Files";
    private static final String TAG_XML_DESTINATION = "destination";
    private static final String TAG_XML_ITEM = "item";
    private static final String TAG_XML_EXPIRATION = "expiration";
    private static final String TAG_XML_NAME = "name";

    private static final ConfigMasterMGC configMasterMGC = ConfigMasterMGC.getConfigSingleton();

    /**
     * Metodo que toma un xml el cual solo lee los nombres de las nuevas peliculas para obtener todos los tokens de estas nuevas peliculas y regresarlas como arreglo
     *
     * @param xmlDataInStringNewTokens cadena de texto en formato XML que contiene las archivos descargados como peliculas, png, xml, etc. Para este caso usamos los xml de las peliculas DRM
     * @return Regresa un arreglo file el cual contiene la ubicacion de los tokens en XML
     */
    public static File[] getArrayFileToXml(String xmlDataInStringNewTokens, Context context) {
        List<File> listTitleMoviesNewTokens = new ArrayList<>();
        File[] arrFileNewTokens = null;

        try {
            XmlPullParser xmlParser = configXmlParser(xmlDataInStringNewTokens);

            //Ciclo que sigue mientras que no llegue al final del documento
            while (xmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                int eventXmlType = xmlParser.getEventType();

                //Se salta inicio de Documento y final de etiquetas
                if (eventXmlType == XmlPullParser.START_DOCUMENT || eventXmlType == XmlPullParser.END_TAG) {
                    xmlParser.next();
                    continue;
                }

                String cmlparserToString = xmlParser.getText();
                //Valida que el renglon del XML sea de tipo FILE para despues validar si es de tipo DRM
                if (xmlParser.getName().equals(TAG_XML_FILES)) {

                    //Obtiene el atributo DESTINATION del renglon actual del xml
                    String nameDestination = xmlParser.getAttributeValue(null, TAG_XML_DESTINATION);

                    //Valida si el tag es FILE DRM
                    if (nameDestination.equals(configMasterMGC.getPathTokens())) {

                        //Si es DRM FILE avanza a la siguiente linea del XML
                        xmlParser.next();

                        ////// valida si es renglon ITEMS DRM
                        while (xmlParser.getName().equals(TAG_XML_ITEM)) {

                            if (xmlParser.getEventType() == XmlPullParser.START_TAG) {
                                //Del renglon llamado ITEM toma el nombre y lo agrega a una lista
                                String nameTitleMovieWithMovie = xmlParser.getAttributeValue(null, TAG_XML_NAME);

                                File titleNewToken = getFileToDownload(nameTitleMovieWithMovie);

                                if (titleNewToken != null) {
                                    listTitleMoviesNewTokens.add(titleNewToken);
                                }

                            }
                            xmlParser.next();
                        }
                    } else {
                        xmlParser.next();
                    }
                } else {
                    xmlParser.next();
                }

            }

            arrFileNewTokens = covertListToArrayFiles(listTitleMoviesNewTokens);


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrFileNewTokens;

    }

    private static boolean theTitleIsValidated(String name, Context context) {
        TokensMoviesCRUD tokensMoviesCRUD = TokensMoviesCRUD.getInstance(context);
        return tokensMoviesCRUD.existMovieWithTokenValidatedInDb(name);

    }

    private static File[] covertListToArrayFiles(List<File> listTitleMoviesNewTokens) {
        File[] fileToreturn = null;
        if (listTitleMoviesNewTokens != null || listTitleMoviesNewTokens.size() != 0) {
            fileToreturn = listTitleMoviesNewTokens.toArray(new File[listTitleMoviesNewTokens.size()]);
        }
        return fileToreturn;
    }


    //FIXME
    private static File getFileToDownload(String nameTitleMovieWithMovie) {
        File fileNewToken = new File(configMasterMGC.getPathTokens() + "/" + nameTitleMovieWithMovie);
        if (!fileNewToken.exists()) {       //si existe en dbxMultimedia pero no en tokens: entonces regresa nulo
            fileNewToken = null;
        }
        return fileNewToken;
    }

    private static XmlPullParser configXmlParser(String xmlInString) {
        XmlPullParser xmlParser = null;
        try {
            XmlPullParserFactory factory = null;
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xmlParser = factory.newPullParser();
            xmlParser.setInput(new StringReader(xmlInString));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.i("ConfigParser", e.getMessage());
        }
        return xmlParser;
    }


    public static String getDateLastUpdate() {
        String xmlString = StorageContentNewTokens.getXmlInStringNewTokens();

        if (xmlString==null)
            return null;


        XmlPullParser xml = configXmlParser(xmlString);

        String dateLastUpdate = null;
        try {
//            xml.require(XmlPullParser.START_TAG, null, "Actia");
//            dateLastUpdate = xml.getAttributeValue(null, "date");

            while (xml.getEventType() != XmlPullParser.END_DOCUMENT) {
                int eventXmlType = xml.getEventType();

                //Se salta inicio de Documento y final de etiquetas
                if (eventXmlType == XmlPullParser.START_DOCUMENT || eventXmlType == XmlPullParser.END_TAG) {
                    xml.next();
                    continue;
                }


                //Valida que el renglon del XML sea de tipo FILE para despues validar si es de tipo DRM
                if (xml.getName().equals("Actia")) {

                    //Obtiene el atributo DESTINATION del renglon actual del xml
                    dateLastUpdate = xml.getAttributeValue(null, "date");

                    return dateLastUpdate;
                }


            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dateLastUpdate;

    }


}
