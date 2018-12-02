package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 11/12/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class RatingResponse {
    private String message;
    private int rating;

    public String getMessage() {
        return message;
    }

    public int getRating() {
        return rating;
    }
}
