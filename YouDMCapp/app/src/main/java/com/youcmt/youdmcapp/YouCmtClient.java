package com.youcmt.youdmcapp;

import com.youcmt.youdmcapp.model.User;
import com.youcmt.youdmcapp.model.Video;


import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public interface YouCmtClient {
    @GET("video/info")
    Call<Video> videoWithUrl(@Query("vid") String url,
                             @HeaderMap HashMap<String,String> headerMa);

    @POST("user/register")
    Call<ResponseBody> registerUser(@Body User user, @HeaderMap HashMap<String,String> headerMap);

    @GET("user/info")
    Call<User> login( @Query("username") String response,
                       @HeaderMap HashMap<String,String> headerMap);
}
