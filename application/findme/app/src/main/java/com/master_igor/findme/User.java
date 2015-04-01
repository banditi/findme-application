package com.master_igor.findme;

import android.graphics.Bitmap;

import org.joda.time.DateTime;

public class User {

    private String name;
    private int idvk;
    private double latitude;
    private double longitude;
    private int distance;
    private String img;
    private Bitmap imgBtm = null;

    public User() { }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIdvk() {
        return idvk;
    }

    public void setIdvk(int idvk) {
        this.idvk = idvk;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Bitmap getImgBtm() {
        return imgBtm;
    }

    public void setImg(Bitmap imgBtm) {
        this.imgBtm = imgBtm;
    }
}