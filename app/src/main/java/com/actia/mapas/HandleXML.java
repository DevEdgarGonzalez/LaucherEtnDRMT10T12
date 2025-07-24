package com.actia.mapas;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

///**
// * Created by Omar Sevilla  on 06/04/2017.
// */

public class HandleXML {
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Context context;

    public HandleXML(Context context){
        this.context = context;
    }

    @SuppressWarnings("unused")
    public boolean existXML(String path){
        File xmlFile = new File(path);
        return xmlFile.exists() && xmlFile.canRead() && xmlFile.isFile() && xmlFile.length() >0;
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    private Document getDomElement(String path){
        Document doc = null;
        InputStream is = null;
        try{
            File file = new File(path);
            is = new FileInputStream(file.getPath());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(is));
            doc.getDocumentElement().normalize();
        }catch (ParserConfigurationException e){
            Log.e("Error Doc Element", e.getMessage());
            return null;
        }catch (SAXException e){
            Log.e("Error Doc Element", e.getMessage());
            return null;
        }catch (IOException e){
            Log.e("Error Doc Element", e.getMessage());
            return null;
        }finally{
            try{
                if(is != null)
                    is.close();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        return doc;
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    public Document getStreamDomElement(InputStream is){
        Document doc = null;
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(is));
            doc.getDocumentElement().normalize();
        }catch(ParserConfigurationException e){
            Log.e("Error Doc element", e.getMessage());
            return null;
        }catch (SAXException e){
            Log.e("Error Doc element", e.getMessage());
            return null;
        }catch (IOException e){
            Log.e("Error Doc element", e.getMessage());
            return null;
        }finally{
            try{
                if(is != null){
                    is.close();
                }
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        return doc;
    }

    public ArrayList<String> parsePlayedXMLFile(String pathPlayedMoviesXML, Document doc){
        ArrayList<String> result = new ArrayList<>();

        if(doc == null && pathPlayedMoviesXML != null){
            doc = getDomElement(pathPlayedMoviesXML);
        }
        if(doc != null){
            doc.getDocumentElement().normalize();

            NodeList nodelist = doc.getElementsByTagName("SSID");
            NodeList pass = doc.getElementsByTagName("PASS");

            for(int i = 0; i< nodelist.getLength(); i++){
                Node node = nodelist.item(i);
                Element fstElement = (Element) node;
                String nameList = fstElement.getTextContent();
                result.add(nameList);
                System.out.println(nameList);
            }

            for(int i = 0; i<pass.getLength(); i++){
                Node node = pass.item(i);
                Element fstElement = (Element) node;
                String nameList = fstElement.getTextContent();
                result.add(nameList);
                System.out.println(nameList);
            }
        }
        return  result;
    }

    public void replaceDocument(String pathPlayedMoviesXML, String ssidAux, String passAux){
        Log.d("HNDLXML createBlankDoc", "creating blank document");

        try{
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();

            Element root = doc.createElement("Actia");
            doc.appendChild(root);
            Element playlistelement = doc.createElement("configuration");
            Element ssid = doc.createElement("SSID");
            ssid.setTextContent(ssidAux);
            Element pass = doc.createElement("PASS");
            pass.setTextContent(passAux);
            playlistelement.appendChild(ssid);
            playlistelement.appendChild(pass);
            root.appendChild(playlistelement);

            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File file = new File(pathPlayedMoviesXML);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            Log.d("HANDLEXML replaceDoc", "Done. File Replaced");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
