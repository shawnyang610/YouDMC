package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/13/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */
public class UpdateCommentRequest {
    private int comment_id;
    private String text;
    public UpdateCommentRequest(int id, String newText)
    {
        comment_id = id;
        text = newText;
    }
}
