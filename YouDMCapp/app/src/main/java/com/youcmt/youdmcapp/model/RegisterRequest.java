package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/1/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public RegisterRequest(String username, String password, String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
