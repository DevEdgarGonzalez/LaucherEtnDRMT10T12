package com.actia.mapas;

public class Info_RouteJson {
    private String fecha;
    private String hora;
    private Double latitud;
    private Double longitud;
    private Double velocidad;
    private String rumbo;
    private Boolean gpsOk;

    public Double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Double velocidad) {
        this.velocidad = velocidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getRumbo() {
        return rumbo;
    }

    public void setRumbo(String rumbo) {
        this.rumbo = rumbo;
    }

    public Boolean getGpsOk() {
        return gpsOk;
    }

    public void setGpsOk(Boolean gpsOk) {
        this.gpsOk = gpsOk;
    }
}
