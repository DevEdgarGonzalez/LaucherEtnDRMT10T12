package com.actia.mensajeria;

public class Info_Message {
    private String mIdMessage;
    private String mMessageText;
    private String mTipo;
    private String mFecha;
    private String mHora;

    public String getIdMessage() {
        return mIdMessage;
    }

    public void setIdMessage(String mIdMessage) {
        this.mIdMessage = mIdMessage;
    }

    public String getMessageText() {
        return mMessageText;
    }

    public void setMessageText(String mMessageText) {
        this.mMessageText = mMessageText;
    }

    public String getTipo() {
        return mTipo;
    }

    public void setTipo(String tipo) {
        this.mTipo = tipo;
    }

    public String getFecha() {
        return mFecha;
    }

    public void setFecha(String fecha) {
        this.mFecha = fecha;
    }

    public String getHora() {
        return mHora;
    }

    public void setHora(String hora) {
        this.mHora = hora;
    }
}
