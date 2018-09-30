package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.youcmt.youdmcapp.model.Video;

/**
 * Created by Stanislav Ostrovskii on 9/30/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class VideoActivity extends AppCompatActivity {
    private static final String EXTRA_VIDEO = "com.youcmt.youdmcapp.model.video_info";
    private Video mVideo;

    /**
     * Use this method to create an intent for this activity.
     * @param context parent activity context
     * @param video the video to display
     * @return intent to pass in to startActivity()
     */
    public static Intent newIntent(Context context, Video video)
    {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mVideo = getIntent().getParcelableExtra(EXTRA_VIDEO);
    }
}
