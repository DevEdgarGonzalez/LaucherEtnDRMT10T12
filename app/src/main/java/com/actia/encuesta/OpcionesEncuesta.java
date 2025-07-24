package com.actia.encuesta;

public class OpcionesEncuesta {
    private String mPregunta_id;
    private String mOpcion_id;
    private String mTextOpcion;

    public OpcionesEncuesta(String pregunta_id, String opcion_id, String textOpcion){

        this.mPregunta_id = pregunta_id;
        this.mOpcion_id = opcion_id;
        this.mTextOpcion = textOpcion;

    }

    public String getPregunta_id() {
        return mPregunta_id;
    }

    public String getOpcion_id() {
        return mOpcion_id;
    }

    public String getTextoOpcion() {
        return mTextOpcion;
    }

}
