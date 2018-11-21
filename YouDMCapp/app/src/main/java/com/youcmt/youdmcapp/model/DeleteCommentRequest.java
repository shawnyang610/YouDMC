package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/13/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */
public class DeleteCommentRequest {
    private int comment_id;
    public DeleteCommentRequest(int id)
    {
        comment_id = id;
    }
}
