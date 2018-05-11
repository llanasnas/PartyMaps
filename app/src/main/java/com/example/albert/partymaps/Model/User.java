package com.example.albert.partymaps.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gerard on 21/01/2018.
 */

public class User implements Parcelable{

    private String name;
    private String mail;
    private String uid;
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


    protected User(Parcel in) {
        name = in.readString();
        mail = in.readString();
        uid = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeString(uid);
        dest.writeString(date);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User() {
    }
    public String getUid(){ return uid; }

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

    @Override
    public int describeContents() {
        return 0;
    }


}
