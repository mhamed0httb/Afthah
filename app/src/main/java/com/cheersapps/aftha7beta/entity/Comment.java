package com.cheersapps.aftha7beta.entity;

/**
 * Created by Mhamed on 15-11-16.
 */

public class Comment {

    private String id,text,postId;
    private String owner;
    private String date,time;

    public Comment(String id, String text, String postId, String owner, String date, String time) {
        this.id = id;
        this.text = text;
        this.postId = postId;
        this.owner = owner;
        this.date = date;
        this.time = time;
    }

    public Comment(String text, String postId, String owner, String date, String time) {
        this.text = text;
        this.postId = postId;
        this.owner = owner;
        this.date = date;
        this.time = time;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", postId='" + postId + '\'' +
                ", owner='" + owner + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
