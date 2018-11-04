package com.youcmt.youdmcapp;

import com.youcmt.youdmcapp.model.CommentPostRequest;
import com.youcmt.youdmcapp.model.CommentResponse;
import com.youcmt.youdmcapp.model.LoginRequest;
import com.youcmt.youdmcapp.model.LoginResponse;
import com.youcmt.youdmcapp.model.RegisterRequest;
import com.youcmt.youdmcapp.model.Video;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public interface ApiEndPoint {

    @GET("video/info")
    Call<Video> videoWithUrl(@Query("vid") String url,
                             @HeaderMap HashMap<String,String> headerMap);

    @POST("user/register")
    Call<LoginResponse> registerUser(@Body RegisterRequest registerRequest,
                                     @HeaderMap HashMap<String,String> headerMap);

    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest,
                       @HeaderMap HashMap<String,String> headerMap);

    @GET("comment")
    Call<CommentResponse> loadComments(@Query("vid") String url,
                                       @HeaderMap HashMap<String, String> headerMap);

    @POST("comment")
    Call<ResponseBody> postComment(@Body CommentPostRequest postRequest,
            @HeaderMap HashMap<String,String> headerMap);

    @GET("check_token")
    Call<ResponseBody> checkToken (@Header("Authorization") String authorization);

    @POST("user/logout_refresh")
    Call<ResponseBody> logoutRefresh (@Header("Authorization") String authorization);

    @POST("user/logout_access")
    Call<ResponseBody> logoutAccess (@Header("Authorization") String authorization);
}
