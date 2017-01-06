package com.cheersapps.aftha7beta.entity;

/**
 * Created by Mohamed on 1/3/2017.
 */

public class MyNotif {
    private String recipient;


    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public MyNotif() {
    }

    public MyNotif(String recipient) {
        this.recipient = recipient;
    }
}
