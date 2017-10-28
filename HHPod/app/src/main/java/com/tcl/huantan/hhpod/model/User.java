package com.tcl.huantan.hhpod.model;

/**
 * Created by huantan on 8/16/16.
 * get and set for user
 */
public class User {
    private int mId;             // the user's id
    private String mName;        // the user's name
    private String mPassword;    // the login password
    private String mEmail;       // the register email
    private String mTel;         // the register telephone

    public User() {
    }

    public User(String name, String password, String emil, String tel) {
        this.mName = name;
        this.mPassword = password;
        this.mEmail = emil;
        this.mTel = tel;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getTel() {
        return mTel;
    }

    public void setTel(String tel) {
        this.mTel = tel;
    }
}
