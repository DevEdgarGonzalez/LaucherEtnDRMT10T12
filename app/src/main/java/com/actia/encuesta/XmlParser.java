package com.actia.encuesta;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    private final String ns = null;

    public XmlParser() {
    }

    public InfoEncuesta parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");
            parser.nextTag();
            return leeXml(parser);
        }catch (XmlPullParserException | IOException ex){
            Log.e("TAG", "parse: ", ex);
            return new InfoEncuesta("", 0, "",0, new ArrayList<PreguntasEncuesta>());
        }finally {
            in.close();
        }
    }

    private InfoEncuesta leeXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        String encuesta_client = null;
        int encuesta_id = 0;
        int form_id = 0;
        String encuesta_msg = null;
        List<PreguntasEncuesta> preguntas = null;
        parser.require(XmlPullParser.START_TAG, ns, "xmlgui");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("encuesta")) {
                encuesta_client = leeEncuestaClient(parser);
            } else if (name.equals("encuesta_id")) {
                encuesta_id = Integer.parseInt(readEncuestaId(parser));
            } else if (name.equals("encuesta_msg")) {
                encuesta_msg = leeEncuestaMsg(parser);
            } else if (name.equals("form")) {
                form_id = Integer.parseInt(leeFormId(parser));
                preguntas = leePreguntas(parser);
            } else {
                skip(parser);
            }
        }
        return new InfoEncuesta(encuesta_client, encuesta_id, encuesta_msg, form_id, preguntas);
    }

    private List<PreguntasEncuesta> leePreguntas(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "form");
        String name = null;
        String label = null;
        String type = null;
        String required = null;
        String optionId = null;
        String optionText = null;
        String options = null;
        List<OpcionesEncuesta> optionslist = null;
        List<PreguntasEncuesta> preguntasEncuestaList = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String field = parser.getName();
            if (field.equals("field")) {
                name = parser.getAttributeValue(null, "name");
                label = parser.getAttributeValue(null, "label").split(":")[1];
                type = parser.getAttributeValue(null, "type");
                required = parser.getAttributeValue(null, "required");
                options = parser.getAttributeValue(null, "options");
                optionslist = new ArrayList<>();
                for (int i = 0; i < options.split(",").length; i++) {
                    optionId = options.split(",")[i].split(":")[0];
                    optionText = options.split(",")[i].split(":")[1];
                    optionslist.add(new OpcionesEncuesta(name, optionId, optionText));
                }
                preguntasEncuestaList.add(new PreguntasEncuesta(name, label, type, required, optionslist));

                parser.nextTag();
            } else {
                skip(parser);
            }
        }
        return preguntasEncuestaList;
    }

    private String leeEncuestaClient(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "encuesta");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "encuesta");
        return title;
    }

    private String leeEncuestaMsg(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "encuesta_msg");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "encuesta_msg");
        return title;
    }

    private String leeFormId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "form");

        String title = null;
        String form = parser.getName();
        title = parser.getAttributeValue(null, "id");
        return title;
    }

    private String readEncuestaId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "encuesta_id");
        String encuesta_id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "encuesta_id");
        return encuesta_id;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
