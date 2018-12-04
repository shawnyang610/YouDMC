package com.youcmt.youdmcapp.model;

import java.util.List;

/**
 * Created by Stanislav Ostrovskii on 12/4/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Objects in the model package represent either a JSON
 * request or response to a Retrofit call
 */

public class WhatsHot {
    //TODO can we rename this to videos? Hideous!
    private List<Video> message;

    public List<Video> getVideoList() {
        return message;
    }
}
