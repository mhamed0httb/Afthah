package com.cheersapps.aftha7beta.entity;

import java.util.Date;

/**
 * Created by Mohamed on 11/29/2016.
 */

public class Message implements Comparable<Message> {
    private String id;
    private String Object;
    private String context;
    private String sender;
    private String recipient;
    private Date date;
    private String todayDate;


    public Message() {
    }



    /*public Message(String id, String object, String context, String sender, String recipient) {
        this.id = id;
        Object = object;
        this.context = context;
        this.sender = sender;
        this.recipient = recipient;
    }/*

   /* public Message(String object, String context, String sender, String recipient) {
        Object = object;
        this.context = context;
        this.sender = sender;
        this.recipient = recipient;
    }*/

    public Message(String context, String recipient, String sender, String todayDate) {
        this.context = context;
        this.recipient = recipient;
        this.sender = sender;
        this.todayDate = todayDate;
    }

    public Message(String context, String recipient) {
        this.context = context;
        this.recipient = recipient;
    }

    public Message(String context, String recipient, String sender) {
        this.context = context;
        this.recipient = recipient;
        this.sender = sender;
    }

    public Message(String context, String recipient, String sender, Date date) {
        this.context = context;
        this.sender = sender;
        this.recipient = recipient;
        this.date = date;
    }

    public Message(String id, String context, Date date) {
        this.id =id;
        this.context = context;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return Object;
    }

    public void setObject(String object) {
        Object = object;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }
    /* @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!context.equals(message.context)) return false;
        if (!sender.equals(message.sender)) return false;
        return recipient.equals(message.recipient);

    }

    @Override
    public int hashCode() {
        int result = context.hashCode();
        result = 31 * result + sender.hashCode();
        result = 31 * result + recipient.hashCode();
        return result;
    }*/

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (Object != null ? !Object.equals(message.Object) : message.Object != null) return false;
        return date != null ? date.equals(message.date) : message.date == null;

    }



    @Override
    public int compareTo(Message another) {
        return getDate().compareTo(another.getDate());
    }
}
