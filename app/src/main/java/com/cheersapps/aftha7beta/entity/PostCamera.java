package com.cheersapps.aftha7beta.entity;

import android.net.Uri;

import com.darsh.multipleimageselect.models.Image;

import java.util.ArrayList;

/**
 * Created by Mhamed on 05-01-17.
 */

public class PostCamera {

    private Uri imUri;
    private String PostDesc;
    private double latLocation,longLoction = 0;
    private ArrayList<Image> listImages;

    public PostCamera(Uri imUri, String postDesc, double latLocation, double longLoction) {
        this.imUri = imUri;
        this.PostDesc = postDesc;
        this.latLocation = latLocation;
        this.longLoction = longLoction;
    }

    public PostCamera(String postDesc, double latLocation, double longLoction) {
        PostDesc = postDesc;
        this.latLocation = latLocation;
        this.longLoction = longLoction;
    }

    public PostCamera() {
    }

    public Uri getImUri() {
        return imUri;
    }

    public void setImUri(Uri imUri) {
        this.imUri = imUri;
    }

    public String getPostDesc() {
        return PostDesc;
    }

    public void setPostDesc(String postDesc) {
        PostDesc = postDesc;
    }

    public double getLatLocation() {
        return latLocation;
    }

    public void setLatLocation(double latLocation) {
        this.latLocation = latLocation;
    }

    public double getLongLoction() {
        return longLoction;
    }

    public void setLongLoction(double longLoction) {
        this.longLoction = longLoction;
    }

    public ArrayList<Image> getListImages() {
        return listImages;
    }

    public void setListImages(ArrayList<Image> listImages) {
        this.listImages = listImages;
    }
}
