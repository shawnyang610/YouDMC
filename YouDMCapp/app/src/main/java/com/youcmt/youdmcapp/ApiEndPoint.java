package com.youcmt.youdmcapp;

import com.youcmt.youdmcapp.model.CommentPostRequest;
import com.youcmt.youdmcapp.model.CommentResponse;
import com.youcmt.youdmcapp.model.DeleteCommentRequest;
import com.youcmt.youdmcapp.model.Email;
import com.youcmt.youdmcapp.model.LoginRequest;
import com.youcmt.youdmcapp.model.LoginResponse;
import com.youcmt.youdmcapp.model.RateRequest;
import com.youcmt.youdmcapp.model.RatingResponse;
import com.youcmt.youdmcapp.model.RegisterRequest;
import com.youcmt.youdmcapp.model.ReplyPostRequest;
import com.youcmt.youdmcapp.model.ResetPasswordRequest;
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

    @GET("comment/get/comments")
    Call<CommentResponse> loadComments(@Query("vid") String url,
                                       @HeaderMap HashMap<String, String> headerMap);

    @GET("comment/get/comments")
    Call<CommentResponse> loadReplies(@Query("parent_comment_id") String url,
                                       @HeaderMap HashMap<String, String> headerMap);

    @POST("comment/post/user")
    Call<ResponseBody> postComment(@Header("Authorization") String authorization,@Body CommentPostRequest postRequest,
            @HeaderMap HashMap<String,String> headerMap);

    @POST("comment/post/user")
    Call<ResponseBody> postReply(@Header("Authorization") String authorization,@Body ReplyPostRequest postRequest,
                                   @HeaderMap HashMap<String,String> headerMap);


    @POST("comment/post/guest")
    Call<ResponseBody> postCommentGuest(@Body CommentPostRequest postRequest,
                                   @HeaderMap HashMap<String,String> headerMap);

    @POST("comment/post/guest")
    Call<ResponseBody> postReplyGuest(@Body ReplyPostRequest postRequest,
                                        @HeaderMap HashMap<String,String> headerMap);

    @GET("check_token")
    Call<ResponseBody> checkToken (@Header("Authorization") String authorization);

    @POST("user/logout_refresh")
    Call<ResponseBody> logoutRefresh (@Header("Authorization") String authorization);

    @POST("user/logout_access")
    Call<ResponseBody> logoutAccess (@Header("Authorization") String authorization);

    @POST("rate_comment")
    Call<RatingResponse> postRating (@Header("Authorization") String authorization, @Body RateRequest rateRequest);

    @POST("refresh_token")
    Call<ResponseBody> refreshToken (@Header("Authorization") String authorization);

    @POST("user/confirm_email")
    Call<ResponseBody> confirmEmail (@Body Email email, @HeaderMap HashMap<String, String> headerMap);

    @POST("user/reset_password")
    Call<ResponseBody> resetPassword (@Body ResetPasswordRequest request, @HeaderMap HashMap<String, String> headerMap);

    @POST("comment/delete")
    Call<ResponseBody> deleteComment(@Header("Authorization") String authorization,
                                     @Body DeleteCommentRequest request,
                                     @HeaderMap HashMap<String, String> headerMap);
}
