package com.example.albert.partymaps;

/**
 * Created by Gerard on 01/02/2018.
 */

public class Event {

    private String name;
    private String music_type;
    private String description;
    private String locality;
    private String date;
    private String time;

    public Event() {
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
}
