package com.actia.drm.auto_tokens;

/**
 * Created by Edgar Gonzalez on 02/04/2018.
 * Actia de Mexico S.A. de C.V..
 */

public class TokenMovie {

    private int id;
    private String titleMovie;
    private int registrationStatus;
    private int numberOfErrors;
    private String xmlLastDateUpdate;
    private String tokenLastUpdateAttemp;

    public TokenMovie() {
    }

    public TokenMovie(int id, String titleMovie, int registrationStatus, int numberOfErrors, String xmlLastDateUpdate, String tokenLastUpdateAttemp) {
        this.id = id;
        this.titleMovie = titleMovie;
        this.registrationStatus = registrationStatus;
        this.numberOfErrors = numberOfErrors;
        this.xmlLastDateUpdate = xmlLastDateUpdate;
        this.tokenLastUpdateAttemp = tokenLastUpdateAttemp;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitleMovie() {
        return titleMovie;
    }

    public void setTitleMovie(String titleMovie) {
        this.titleMovie = titleMovie;
    }

    public int getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(int registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public String getXmlLastDateUpdate() {
        return xmlLastDateUpdate;
    }

    public void setXmlLastDateUpdate(String xmlLastDateUpdate) {
        this.xmlLastDateUpdate = xmlLastDateUpdate;
    }

    public String getTokenLastUpdateAttemp() {
        return tokenLastUpdateAttemp;
    }

    public void setTokenLastUpdateAttemp(String tokenLastUpdateAttemp) {
        this.tokenLastUpdateAttemp = tokenLastUpdateAttemp;
    }
}
