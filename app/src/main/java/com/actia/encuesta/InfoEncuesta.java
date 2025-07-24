package com.actia.encuesta;

import java.util.List;

public class InfoEncuesta {

    private String mEncuesta_client;
    private int mEncuesta_id;
    private String mEncuesta_msg;
    private int mForm_id;
    private List<PreguntasEncuesta> mPreguntasEncuesta;

    public InfoEncuesta(String encuesta_client, int encuesta_id, String encuesta_msg, int form_id, List<PreguntasEncuesta> preguntasEncuesta){

        this.mEncuesta_client = encuesta_client;
        this.mEncuesta_id = encuesta_id;
        this.mEncuesta_msg = encuesta_msg;
        this.mForm_id = form_id;
        this.mPreguntasEncuesta = preguntasEncuesta;

    }

    public String getEncuesta_client() {
        return mEncuesta_client;
    }

    public int getEncuesta_id() {
        return mEncuesta_id;
    }

    public String getEncuesta_msg(){return mEncuesta_msg;}

    public int getForm_id(){return mForm_id;}

    public List<PreguntasEncuesta> getPreguntas() {
        return mPreguntasEncuesta;
    }

}
