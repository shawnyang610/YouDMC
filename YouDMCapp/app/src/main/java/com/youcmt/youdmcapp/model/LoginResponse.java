package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/1/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */


public class LoginResponse {
    private String message;
    private String role;
    private int id;
    private String username;
    private String email;
    private String profile_img;
    private String reg_date;
    private String access_token;
    private String refresh_token;

    public String getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public String getReg_date() {
        return reg_date;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getMessage()
    {
        return message;
    }
}
