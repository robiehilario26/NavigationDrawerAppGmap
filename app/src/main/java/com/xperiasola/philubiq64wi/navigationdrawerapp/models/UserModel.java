package com.xperiasola.philubiq64wi.navigationdrawerapp.models;

/**
 * Created by philUbiq64wi on 1/29/2018.
 */

public class UserModel {




    private String user_id;
    private String user_email;
    private String user_fullName;
    private String user_age;
    private String user_password;
    private String user_hint;


    //** Call by fetchContacts created inside DatabaseHelper
    public UserModel(String user_id, String user_email, String user_fullName,
                     String user_age, String user_password, String user_hint) {
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_fullName = user_fullName;
        this.user_age = user_age;
        this.user_password = user_password;
        this.user_hint = user_hint;
    }


    public UserModel() {

    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_fullName() {
        return user_fullName;
    }

    public void setUser_fullName(String user_fullName) {
        this.user_fullName = user_fullName;
    }

    public String getUser_age() {
        return user_age;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_hint() {
        return user_hint;
    }

    public void setUser_hint(String user_hint) {
        this.user_hint = user_hint;
    }
}
