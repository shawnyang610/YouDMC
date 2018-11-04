package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/1/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
}
