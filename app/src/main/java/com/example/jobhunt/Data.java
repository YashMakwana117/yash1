package com.example.jobhunt;

public class Data {
    private String id;
    private String photourl;

    public Data() {
        // Default constructor required for Firebase
    }

    public Data(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}