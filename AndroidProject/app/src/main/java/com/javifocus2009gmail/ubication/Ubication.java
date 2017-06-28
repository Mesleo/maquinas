package com.javifocus2009gmail.ubication;

import java.io.Serializable;

/**
 * Created by jbenitez on 13/10/16.
 */

public class Ubication implements Serializable {

    private String id;
    private String ubication;
    private int machine;
    private String nameMachine;
    private String date;
    private String user;
    private String lat;
    private String lng;

    Ubication(){

    }

    public Ubication(String id, String ubication, String date, String user, String lati, String lngi) {
        this.id = id;
        this.ubication = ubication;
        this.date = date;
        this.user = user;
        this.lat = lati;
        this.lng = lngi;
    }

    public Ubication(String ubication, int machine, String date, String user, String lati, String lngi) {
        this.ubication = ubication;
        this.machine = machine;
        this.date = date;
        this.user = user;
        this.lat = lati;
        this.lng = lngi;
    }

    public Ubication(String id, String ubi, String dateFormat, String user, String latitude, String longitude, String machine, int idMachine) {
        this.id = id;
        this.ubication = ubi;
        this.date = dateFormat;
        this.user = user;
        this.lat = latitude;
        this.lng = longitude;
        this.nameMachine = machine;
        this.machine = idMachine;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getMachine() {
        return machine;
    }

    public void setMachine(int machine) {
        this.machine = machine;
    }

    public String getNameMachine() {
        return nameMachine;
    }

    public void setNameMachine(String nameMachine) {
        this.nameMachine = nameMachine;
    }
}
