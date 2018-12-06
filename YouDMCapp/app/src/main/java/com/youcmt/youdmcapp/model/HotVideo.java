package com.youcmt.youdmcapp.model;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 * TODO this particular object is unnecessary we can use regular Video instead
 */

public class HotVideo {
    private String date;
    private String title;
    private String vid;

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getVid() {
        return vid;
    }
}

