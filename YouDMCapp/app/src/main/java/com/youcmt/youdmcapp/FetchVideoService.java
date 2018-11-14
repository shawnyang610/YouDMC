package com.youcmt.youdmcapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.youcmt.youdmcapp.model.Video;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.youcmt.youdmcapp.Constants.*;

/**
 * Created by Stanislav Ostrovskii on 9/27/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class FetchVideoService extends IntentService {
    private final static String TAG = "FetchVideoService";
    final static int SUCCESS = 0;
    final static int FAIL = 1;

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

            HashMap header = new HashMap();
            header.put("Content-Type", "application/json");
            ApiEndPoint client = RetrofitClient.getApiEndpoint();
            Call<Video> response = client.videoWithUrl(url, header);
            response.enqueue(new Callback<Video>() {
                @Override
                public void onResponse(@NonNull Call<Video> call, @NonNull Response<Video> response) {
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
                    Log.d(TAG, "onFailure() called");
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
            returnIntent.putExtra(EXTRA_VIDEO, video[0]);
            Log.d(TAG, video[0].getTitle());
        }
        sendBroadcast(returnIntent);
    }

}
