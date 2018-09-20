package com.youcmt.youdmcapp;

import com.google.gson.JsonObject;
import com.youcmt.youdmcapp.model.User;
import com.youcmt.youdmcapp.model.VideoRequest;


import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by stan_ on 9/18/2018.
 */

public interface YouCmtClient {
    @GET("video/info?vid={url}")
    Call<VideoRequest> videoWithUrl(@Path("url") String url, @Body VideoRequest request);

    @POST("user/register")
    Call<ResponseBody> registerUser(@Body User user, @HeaderMap HashMap<String,String> headerMap);

    @GET("user/info")
    Call<User> login( @Query("username") String response,
                       @HeaderMap HashMap<String,String> headerMap);
}
