package com.youcmt.youdmcapp;

import com.youcmt.youdmcapp.model.User;
import com.youcmt.youdmcapp.model.VideoRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by stan_ on 9/18/2018.
 */

public interface YouCmtClient {
    @GET("/video/info?vid={url}")
    Call<VideoRequest> videoWithUrl(@Path("url") String url, @Body VideoRequest request);

    @POST("/user/register")
    Call<User> registerUser(@Body User user);

    @GET("user/info?username={username}&email={email}")
    Call<User> login(@Path ("username") String username, @Path ("email") String email);
}
