package com.example.bemechat.Models;

public class User {

    private String uid, name, phonenumber, profileimage;

    public User() {

    }

    public User(String uid, String name, String phonenumber, String profileimage) {
        this.uid = uid;
        this.name = name;
        this.phonenumber = phonenumber;
        this.profileimage = profileimage;
    }

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

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

}
