package com.cheersapps.aftha7beta.entity;

/**
 * Created by Mhamed on 24-11-16.
 */

public class Like {

    private String id,postId,ownerId;


    public Like() {
    }

    public Like(String id, String postId, String ownerId) {
        this.id = id;
        this.postId = postId;
        this.ownerId = ownerId;
    }

    public Like(String postId, String ownerId) {
        this.postId = postId;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Like{" +
                "id='" + id + '\'' +
                ", postId='" + postId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
