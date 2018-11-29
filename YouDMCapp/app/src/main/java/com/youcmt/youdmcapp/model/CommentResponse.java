package com.youcmt.youdmcapp.model;

import java.util.List;

/**
 * Created by Stanislav Ostrovskii on 10/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class CommentResponse {
    private List<Comment> comments;

    public List<Comment> getCommentList() {
        return comments;
    }
}
