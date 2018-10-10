package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.youcmt.youdmcapp.model.Video;

import static com.youcmt.youdmcapp.Constants.EXTRA_VIDEO;

/**
 * Created by Stanislav Ostrovskii on 9/30/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "VideoActivity";
    private Video mVideo;
    private TextView mTitleView;

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
        mTitleView = findViewById(R.id.title_tv);
        mTitleView.setText(mVideo.getTitle());
        mTitleView.setTextSize(getTextSize(mVideo.getTitle()));
        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        if(youTubePlayerFragment!=null) youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
    }

    /**
     * Seeks to choose an appropriate text size depending on video title length
     * @param string video title length
     * @return suggested text size
     */
    private float getTextSize(String string) {
        int length = string.length();
        if(length<20) return 32;
        else if(length<40) return 24;
        else return 18;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(mVideo.getVid());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d(TAG, "on Failure called:  " + youTubeInitializationResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
