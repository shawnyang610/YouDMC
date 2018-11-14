package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class CommentPostRequest {
    private String vid;
    private String text;
    private int user_id;

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
