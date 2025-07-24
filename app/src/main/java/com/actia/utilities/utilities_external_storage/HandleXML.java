package com.actia.utilities.utilities_external_storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

/**
 * Handle xml file
 */
public class HandleXML {
	Context context;
	
	public HandleXML(Context context){
		this.context=context;
	}
	
	/**
	 * Get xml document
	 * @param path xml path
	 * @return document
	 */
    public Document getDomElement(String path){
        Document doc = null;
        InputStream is=null;
        try {
        	File file = new File(path);
            is = new FileInputStream(file.getPath());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new InputSource(is));
            doc.getDocumentElement().normalize();
 
            } catch (ParserConfigurationException e) {
                Log.e("Error Doc element: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error Doc element: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error Doc element: ", e.getMessage());
                return null;
            }finally{
            	try {
            		if(is!=null)
            			is.close();
				} catch (IOException e) {
					e.printStackTrace();
                    //noinspection ReturnInsideFinallyBlock
                    return null;
				}
            }
                // return DOM
            return doc;
    }
}

