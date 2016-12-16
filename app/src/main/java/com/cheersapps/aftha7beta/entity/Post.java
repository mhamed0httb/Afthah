package com.cheersapps.aftha7beta.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mhamed on 05/11/2016.
 */
public class Post {

    private String id;
    private int numberLikes;
    private String description,media,mediaType,date,time;
    private double latLocation,LongLocation;
    private String owner;
    private ArrayList<String> listMediaUrl;


    public Post(String id, int numberLikes, String description, String media,String mediaType, String date, String time, double latLocation, double longLocation, String owner) {
        this.id = id;
        this.numberLikes = numberLikes;
        this.description = description;
        this.media = media;
        this.mediaType = mediaType;
        this.date = date;
        this.time = time;
        this.latLocation = latLocation;
        LongLocation = longLocation;
        this.owner = owner;
        listMediaUrl = new ArrayList<String>();
    }

    public Post(int numberLikes, String description, String media,String mediaType, String date, String time, double latLocation, double longLocation, String owner) {
        this.numberLikes = numberLikes;
        this.description = description;
        this.media = media;
        this.mediaType = mediaType;
        this.date = date;
        this.time = time;
        this.latLocation = latLocation;
        LongLocation = longLocation;
        this.owner = owner;
        listMediaUrl = new ArrayList<String>();
    }

    public Post(String description, String media) {
        this.description = description;
        this.media = media;
    }

    public Post() {
        listMediaUrl = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getNumberLikes() {
        return numberLikes;
    }

    public void setNumberLikes(int numberLikes) {
        this.numberLikes = numberLikes;
    }

    public double getLatLocation() {
        return latLocation;
    }

    public void setLatLocation(double latLocation) {
        this.latLocation = latLocation;
    }

    public double getLongLocation() {
        return LongLocation;
    }

    public void setLongLocation(double longLocation) {
        LongLocation = longLocation;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public ArrayList<String> getListMediaUrl() {
        return listMediaUrl;
    }

    public void setListMediaUrl(ArrayList<String> listMediaUrl) {
        this.listMediaUrl = listMediaUrl;
    }



    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", numberLikes=" + numberLikes +
                ", description='" + description + '\'' +
                ", media='" + media + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", latLocation=" + latLocation +
                ", LongLocation=" + LongLocation +
                ", owner='" + owner + '\'' +
                '}';
    }
}
