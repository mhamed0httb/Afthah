package com.cheersapps.aftha7beta.entity;

/**
 * Created by Mhamed on 27-11-16.
 */

public class Media {

    private String id;
    private String downloadURL;
    private String postId;

    public Media(String id, String downloadURL, String postId) {
        this.id = id;
        this.downloadURL = downloadURL;
        this.postId = postId;
    }

    public Media(String downloadURL, String postId) {
        this.downloadURL = downloadURL;
        this.postId = postId;
    }

    public Media() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id='" + id + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                ", postId='" + postId + '\'' +
                '}';
    }
}
