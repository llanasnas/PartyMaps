package com.example.albert.partymaps;

/**
 * Created by Gerard on 21/01/2018.
 */

public class User {

    private String name;
    private String mail;
    private String date;


    public User(String name, String mail, String date) {
        this.name = name;
        this.mail = mail;
        this.date = date;
    }
    public User(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getDate() {
        return date;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
