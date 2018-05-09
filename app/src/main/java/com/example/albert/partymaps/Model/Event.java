package com.example.albert.partymaps.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gerard on 01/02/2018.
 */

public class Event implements Parcelable,Serializable,Comparable<Event>{

    private String name;
    private String music_type;
    private String description;
    private String locality;
    private String id;
    private String date;
    private String time;
    private String ubication;

    protected Event(Parcel in) {
        name = in.readString();
        music_type = in.readString();
        description = in.readString();
        locality = in.readString();
        date = in.readString();
        time = in.readString();
        ubication = in.readString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public Event() {
    }

    public Event(String name, String music_type, String description, String locality, String date, String time, String ubication) {
        this.name = name;
        this.music_type = music_type;
        this.description = description;
        this.locality = locality;
        this.date = date;
        this.time = time;
        this.ubication = ubication;
    }

    public String getName() {
        return name;
    }

    public String getMusic_type() {
        return music_type;
    }

    public String getDescription() {
        return description;
    }

    public String getLocality() {
        return locality;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMusic_type(String music_type) {
        this.music_type = music_type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(music_type);
        dest.writeString(description);
        dest.writeString(locality);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(id);
        dest.writeString(ubication);
    }

    public Date devolverFechaFormateada(){
        Date date = new Date();
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = format.parse(getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public int compareTo(@NonNull Event event) {
        int num = devolverFechaFormateada().compareTo(event.devolverFechaFormateada());
        int b = 0;
        return num;
    }
}
