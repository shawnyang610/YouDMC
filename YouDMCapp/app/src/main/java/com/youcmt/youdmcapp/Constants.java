package com.youcmt.youdmcapp;

import java.util.HashMap;

/**
 * Created by Stanislav Ostrovskii on 9/29/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * This file contains sensitive information that should not be
 * used outside this application.
 */

class Constants {
    static final int ID_GUEST = 2;
    static final int ID_ADMIN = 1;
    static final int ID_NONE = 0;
    static final String LOGGED_IN = "com.youdmcapp.youcmt.logged_in";
    static final String USER_ID = "com.youdmcapp.youcmt.user_id";
    static final String USERNAME = "com.youdmcapp.youcmt.username";
    static final String ACCESS_TOKEN = "com.youdmcapp.youcmt.access_token";
    static final String REFRESH_TOKEN = "com.youdmcapp.youcmt.refresh_token";
    static final String PROFILE_IMG = "com.youdmcapp.youcmt.profile_image";
    static final String YOUTUBE_API_KEY = "AIzaSyCfb5XZ10Ni7yFSQq8Kv5FTRaxSTtJIqxU";
    static final String EXTRA_VIDEO = "com.youcmt.youdmcapp.extra_video";
    static final String EXTRA_VIDEO_STATUS = "com.youcmt.youdmcapp.extra_video_status";
    static final String EXTRA_VIDEO_URL = "com.youcmt.youdmcapp.extra_video_url";
    static final String FETCH_VIDEO_INFO = "com.youcmt.youdmcapp.fetch_video_info";
    static final HashMap jsonHeader()
    {
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        return header;
    }
}