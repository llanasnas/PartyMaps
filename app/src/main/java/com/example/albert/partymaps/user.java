package com.example.albert.partymaps;

/**
 * Created by Gerard on 21/01/2018.
 */

public class user {

    private String name;
    private String mail;
    private String date;


    public user(String name, String mail, String date) {
        this.name = name;
        this.mail = mail;
        this.date = date;
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
}
