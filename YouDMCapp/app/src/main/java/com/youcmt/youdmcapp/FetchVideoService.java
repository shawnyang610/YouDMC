package com.youcmt.youdmcapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.youcmt.youdmcapp.model.Video;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.youcmt.youdmcapp.Constants.BASE_API_URL;

/**
 * Created by Stanislav Ostrovskii on 9/27/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class FetchVideoService extends IntentService {
    private final static String TAG = "IntentService";
    final static int SUCCESS = 0;
    final static int FAIL = 1;
    final static int IN_PROGRESS = 2;
    final static String EXTRA_VIDEO_URL = "com.youcmt.youdmcapp.video_url";
    final static String EXTRA_OUT_MESSAGE = "com.youcmt.youdmcapp.out_message";
    final static String FETCH_VIDEO_INFO = "com.youcmt.youdmcapp.fetch_video_info";
    final static String EXTRA_VIDEO_STATUS = "com.youcmt.youdmcapp.video_statis";
    /**
     * Use this method to create a new Intent for this service
     * @param context
     * @param url - the part of the url to send to the network
     * @return an Intent you can use to start the FetchVideoService
     */
    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, FetchVideoService.class);
        intent.putExtra(EXTRA_VIDEO_URL, url);
        return intent;
    }

    public FetchVideoService() {
        super(FetchVideoService.class.getName());
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null) {
            String url = intent.getStringExtra(EXTRA_VIDEO_URL);
            Log.d(TAG, "URL: " + url);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            HashMap header = new HashMap();
            header.put("Content-Type", "application/json");
            YouCmtClient client = retrofit.create(YouCmtClient.class);
            Call<Video> response = client.videoWithUrl(url, header);
            response.enqueue(new Callback<Video>() {
                @Override
                public void onResponse(Call<Video> call, Response<Video> response) {
                    if(response.code()==200)
                    {
                        Video video = response.body();
                        if(video.getVid()!=null)
                        {
                            returnToActivity(SUCCESS, video);
                        }
                        else returnToActivity(FAIL);

                    }
                    else
                    {
                        Log.d(TAG, String.valueOf(response.code()));
                        returnToActivity(FAIL);
                    }
                }

                @Override
                public void onFailure(Call<Video> call, Throwable t) {
                    returnToActivity(FAIL);
                }
            });

        }
    }

    private void returnToActivity(int statusCode, Video... video)
    {
        Log.d(TAG, "returnToActivity called");
        Intent returnIntent = new Intent();
        returnIntent.setAction(FETCH_VIDEO_INFO);
        returnIntent.putExtra(EXTRA_VIDEO_STATUS, statusCode);
        if(video.length!=0) {
            returnIntent.putExtra(EXTRA_OUT_MESSAGE, video[0].getTitle());
            Log.d(TAG, video[0].getTitle());
        }
        sendBroadcast(returnIntent);
    }

}
