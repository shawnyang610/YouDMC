package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/21/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class UpdateProfileRequest {
    private String new_email;
    private String new_profile_img;
    private String old_password;
    private String new_password;

    public void setNew_email(String new_email) {
        this.new_email = new_email;
    }

    public void setNew_profile_img(String new_profile_img) {
        this.new_profile_img = new_profile_img;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
