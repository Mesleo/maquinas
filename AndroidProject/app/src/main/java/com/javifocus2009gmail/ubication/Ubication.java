package com.javifocus2009gmail.ubication;

import java.io.Serializable;

/**
 * Created by jbenitez on 13/10/16.
 */

public class Ubication implements Serializable {

    private String id;
    private String ubication;
    private String date;
    private String nameUser;
    private String lat;
    private String lng;

    public Ubication(String id, String ubication, String date, String nameUser, String lati, String lngi) {
        this.id = id;
        this.ubication = ubication;
        this.date = date;
        this.nameUser = nameUser;
        this.lat = lati;
        this.lng = lngi;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Ubication(String ubication, String date) {
        this.ubication = ubication;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public String getId() {
        return id;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

}
