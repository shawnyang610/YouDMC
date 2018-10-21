package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.youcmt.youdmcapp.model.Comment;
import com.youcmt.youdmcapp.model.CommentPostRequest;
import com.youcmt.youdmcapp.model.CommentResponse;
import com.youcmt.youdmcapp.model.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.youcmt.youdmcapp.Constants.BASE_API_URL;
import static com.youcmt.youdmcapp.Constants.EXTRA_VIDEO;
import static com.youcmt.youdmcapp.Constants.ID_ADMIN;
import static com.youcmt.youdmcapp.Constants.ID_GUEST;
import static com.youcmt.youdmcapp.Constants.USER_ID;

/**
 * Created by Stanislav Ostrovskii on 9/30/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener
{
    private static final String TAG = "VideoActivity";
    private List<Comment> mComments;
    private Video mVideo;
    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private CommentAdapter mCommentAdapter;
    private EditText mCommentEditText;
    private ImageView mSendButton;
    private int mUserId;
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
        fetchComments();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        mUserId = getPreferences(MODE_PRIVATE).getInt(USER_ID, ID_GUEST);
        mComments = new ArrayList<>(1);
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
        mCommentEditText = findViewById(R.id.new_comment_et);
        mSendButton = findViewById(R.id.send_button);
        mCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0) mSendButton.setVisibility(View.VISIBLE);
                else mSendButton.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment(mCommentEditText.getText().toString());
                mCommentEditText.setText("");
            }
        });
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
        Toast.makeText(this, "Error: Do you have the Youtube app installed?", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void fetchComments()
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        YouCmtClient client = retrofit.create(YouCmtClient.class);
        Call<CommentResponse> call = client.loadComments(mVideo.getVid(), header());
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {

                if(response.code()==200) {
                    CommentResponse commentResponse = response.body();
                    processComments(commentResponse);
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
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Log.d(TAG, "onFailureCalled " );
                Log.d(TAG, t.getMessage());
                displayUnknownError();
            }
        });
    }

    private void postComment(String commentText)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        YouCmtClient client = retrofit.create(YouCmtClient.class);

        CommentPostRequest postRequest = new CommentPostRequest();
        postRequest.setText(commentText);
        postRequest.setVid(mVideo.getVid());
        postRequest.setUser_id(mUserId);

        Call<ResponseBody> response = client.postComment(postRequest, header());
        Log.d(TAG, "URL: " + response.request().url().toString());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "Response code: " + response.code());
                if(response.code()==200)
                {
                    Toast.makeText(getBaseContext(), "Comment posted!", Toast.LENGTH_SHORT).show();
                    mRecyclerView.setVisibility(GONE);
                    findViewById(R.id.comment_pb).setVisibility(View.VISIBLE);
                    fetchComments(); //reload comment list

                }
                else if(response.code()==400)
                {
                    Toast.makeText(getBaseContext(), "Unable to process request!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Unknown error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayUnknownError() {
        Toast.makeText(this, "Unknown error occurred! Oops!", Toast.LENGTH_SHORT).show();
    }

    private void processComments(CommentResponse response)
    {
        mRecyclerView = findViewById(R.id.comment_rv);
        findViewById(R.id.comment_pb).setVisibility(GONE);
        if(response.getCommentList()!=null){
            mComments.clear();
            for(Comment comment: response.getCommentList())
            {
                mComments.add(comment);
                Log.d(TAG, "ParentID: " + comment.getParent_comment_id() +
                    " TopID: " + comment.getTop_comment_id() + " ID: " + comment.getId());
            }
            Log.d(TAG, "We fetched " + mComments.size() + " comments");
        }
        else displayUnknownError();

        if(mComments==null || mComments.size()==0)
        {
            mComments = new ArrayList<>(0);
            mRecyclerView.setVisibility(GONE);
            findViewById(R.id.no_comments_tv).setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);

            findViewById(R.id.no_comments_tv).setVisibility(View.GONE);
            if(mCommentAdapter==null)
            {
                mCommentAdapter = new CommentAdapter(mComments, this);
                mRecyclerView.setAdapter(mCommentAdapter);
            }
            else {
                mCommentAdapter.notifyDataSetChanged();
            }
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private HashMap header()
    {
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        return header;
    }

}
