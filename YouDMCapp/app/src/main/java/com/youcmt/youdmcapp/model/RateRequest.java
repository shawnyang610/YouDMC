package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/12/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class RateRequest {
    private int comment_id;
    private int rating;

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
