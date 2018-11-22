package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.youcmt.youdmcapp.model.Video;

import static android.view.View.GONE;
import static com.youcmt.youdmcapp.Constants.*;

/**
 * Created by Stanislav Ostrovskii on 9/30/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener
{
    private static final String TAG = "VideoActivity";
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_comment);
        if(fragment==null)
        {
            fragment = new CommentFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container_comment, fragment).commit();
        }
        mVideo = getIntent().getParcelableExtra(EXTRA_VIDEO);
        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        if(youTubePlayerFragment!=null) youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(mVideo.getVid());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        findViewById(R.id.youtube_fragment).setVisibility(GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            youTubeInitializationResult.getErrorDialog(VideoActivity.this, 0).show();
        }
        else displayAnnoyingError();
    }

    void displayAnnoyingError()
    {
        Toast.makeText(VideoActivity.this,
                "Error. Do you have the YouTube app installed?",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
