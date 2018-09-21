package com.youcmt.youdmcapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.youcmt.youdmcapp.model.Video;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by stan_ on 9/18/2018.
 */

public class FetchVideoService extends IntentService {

    private final static String EXTRA_VIDEO_URL = "com.youcmt.youdmcapp.video_url";
    /**
     * Use this method to create a new Intent for this service
     * @param context
     * @param url - the full video URL as a String
     * @return an Intent you can use to start the FetchVideoService
     */
    public static Intent newIntent(Context context, String url) throws Exception {
        if(url.contains("="))
        {
            String[] parts = url.split("=");
            url = parts[1];
        }
        else if(url.contains(".be"))
        {
            String[] parts = url.split("/");
            url = parts[3];
        }
        else{
            throw new Exception("Not a valid youtube URL.");
        }
        Intent intent = new Intent(context, FetchVideoService.class);
        intent.putExtra(EXTRA_VIDEO_URL, url);
        return intent;
    }

    public FetchVideoService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null) {
            String url = intent.getStringExtra(EXTRA_VIDEO_URL);
            Video video = new Video("https://youtu.be/sPlhKP0nZII");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }
}
