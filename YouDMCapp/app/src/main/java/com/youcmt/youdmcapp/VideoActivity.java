package com.youcmt.youdmcapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.youcmt.youdmcapp.model.Comment;
import com.youcmt.youdmcapp.model.User;
import com.youcmt.youdmcapp.model.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.youcmt.youdmcapp.Constants.BASE_API_URL;
import static com.youcmt.youdmcapp.Constants.EXTRA_VIDEO;

/**
 * Created by Stanislav Ostrovskii on 9/30/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "VideoActivity";
    private List<Comment> mComments;
    private Video mVideo;
    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private CommentAdapter mCommentAdapter;
    private CommentHolder mCommentHolder;

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
    protected void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        mVideo = getIntent().getParcelableExtra(EXTRA_VIDEO);
        mTitleView = findViewById(R.id.title_tv);
        mTitleView.setText(mVideo.getTitle());
        mTitleView.setTextSize(getTextSize(mVideo.getTitle()));
        mTitleView.setOnClickListener(new View.OnClickListener() {
            private boolean mExpanded = false;
            private Drawable arrowUp = getResources()
                    .getDrawable(R.drawable.ic_arrow_drop_up);
            private Drawable arrowDown = getResources()
                    .getDrawable(R.drawable.ic_arrow_drop_down);
            TextView mDescriptionView = findViewById(R.id.video_description);
            @Override
            public void onClick(View view) {
                if(mExpanded)
                {
                    mDescriptionView.setVisibility(GONE);
                    mTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDown, null);
                }
                else {
                    mDescriptionView.setVisibility(View.VISIBLE);
                    mDescriptionView.setText(mVideo.getDescription());
                    mTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowUp, null);
                }
                mExpanded = !mExpanded;
            }
        });
        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        if(youTubePlayerFragment!=null) youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
        mRecyclerView = findViewById(R.id.comment_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
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
        Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "on Failure called:  " + youTubeInitializationResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void updateRecyclerView()
    {
        fetchComments();
        if(mComments==null)
        {
            mComments = new ArrayList<Comment>(0);
            mRecyclerView.setVisibility(GONE);
            findViewById(R.id.no_comments_tv).setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.no_comments_tv).setVisibility(View.GONE);
        }
        if(mCommentAdapter==null)
        {
            mCommentAdapter = new CommentAdapter(mComments, this);
        }
        else {
            mCommentAdapter.notifyDataSetChanged();
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(mCommentAdapter);
    }

    private void fetchComments()
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        YouCmtClient client = retrofit.create(YouCmtClient.class);
        Call<List<Comment>> call = client.loadComments(mVideo.getVid(), header);
        Log.d(TAG, call.request().url().toString());
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.code()==200) {
                    mComments = response.body();
                    Log.d(TAG, "Success");
                }
                else if(response.code()==404)
                {
                    try {
                        Log.d(TAG, String.valueOf(response.code()));
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), errorMessage.getString("message"), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        displayUnknownError();
                        e.printStackTrace();
                        Log.d(TAG, "IOException");

                    } catch (JSONException j)
                    {
                        displayUnknownError();
                        j.printStackTrace();
                        Log.d(TAG, "JSONException");

                    }
                }
                else {
                    Log.d(TAG, "Other Code");
                    displayUnknownError();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.d(TAG, "onFailureCalled " );
                displayUnknownError();
            }
        });
    }

    private void displayUnknownError() {
        Toast.makeText(this, "Unknown error occurred! Oops!", Toast.LENGTH_SHORT).show();
    }
}
