package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class ReplyPostRequest {
    private String parent_comment_id;
    private String text;
    private int user_id;

    public void setParent_comment_id(String id) {
        this.parent_comment_id = id;
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