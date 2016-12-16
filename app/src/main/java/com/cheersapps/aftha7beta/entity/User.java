package com.cheersapps.aftha7beta.entity;

/**
 * Created by Mhamed on 05/11/2016.
 */
public class User {

    private String uid,name,mail,pass,image;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public User() {
    }

    public User(String uid, String name, String mail, String pass, String image) {
        this.uid = uid;
        this.name = name;
        this.mail = mail;
        this.pass = pass;
        this.image = image;
    }

    public User(String name, String mail, String pass, String image) {
        this.name = name;
        this.mail = mail;
        this.pass = pass;
        this.image = image;
    }
}
