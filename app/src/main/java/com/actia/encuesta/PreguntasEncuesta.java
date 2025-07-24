package com.actia.encuesta;

import java.util.List;

public class PreguntasEncuesta {

    private String mName;
    private String mLabel;
    private String mType;
    private String mRequired;
    private List<OpcionesEncuesta> mOptions;

    public PreguntasEncuesta(String name,String label, String type, String required, List<OpcionesEncuesta> options){
        this.mName = name;
        this.mLabel = label;
        this.mType = type;
        this.mRequired = required;
        this.mOptions = options;
    }


    public String getName() {
        return mName;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getType() {
        return mType;
    }

    public String getRequired() {
        return mRequired;
    }

    public List<OpcionesEncuesta> getOptions() {
        return mOptions;
    }

}
