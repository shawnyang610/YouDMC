package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/13/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class ResetPasswordRequest {
    private String email;
    private String reset_code;
    private String new_password;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setReset_code(String reset_code) {
        this.reset_code = reset_code;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
