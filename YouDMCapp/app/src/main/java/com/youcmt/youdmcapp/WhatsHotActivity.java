package com.youcmt.youdmcapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.HotVideo;
import com.youcmt.youdmcapp.model.WhatsHot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Stanislav Ostrovskii on 12/4/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * This activity contains a list of videos that are currently being discussed.
 * Clicking one will return the video's url to the MainActivity, which will,
 * in turn, launch HotVideoActivity to display the page for that video.
 */

public class WhatsHotActivity extends AppCompatActivity
{
    private static final String TAG = "WhatsHotActivity";
    public static final String EXTRA_VIDEO_URL = "com.youcmt.youdmcapp.video_url";
    private RecyclerView mRecyclerView;
    private HotVideoAdapter mHotVideoAdapter;
    private TextView mNoVidsTv;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_hot);
        mRecyclerView = findViewById(R.id.video_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoVidsTv = findViewById(R.id.no_vids_tv);
        mNoVidsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNoVidsTv.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                fetchWhatsHot();
            }
        });
        mProgressBar = findViewById(R.id.whats_hot_pb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWhatsHot();
    }

    private class HotVideoHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{
        HotVideo mHotVideo;
        TextView mHotVideoTitle;
        TextView mTimeAgoTv;
        public HotVideoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mHotVideoTitle = itemView.findViewById(R.id.video_title);
            mTimeAgoTv = itemView.findViewById(R.id.time_ago);
        }

        public void bindHotVideo(HotVideo video) {
            mHotVideo = video;
            String title = mHotVideo.getTitle();
            //abbreviate the title if it is too long
            if(title.length()>85)
            {
                title = title.substring(0, 80);
                title = title + "...";
            }
            mHotVideoTitle.setText(title);
            String timeAgo = getTimeAgoString(mHotVideo.getDate());
            mTimeAgoTv.setText(timeAgo);
        }

        @Override
        public void onClick(View view) {
            returnVideoResult(mHotVideo.getVid());
        }
    }

    private void returnVideoResult(String vid) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_VIDEO_URL, vid);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class HotVideoAdapter extends RecyclerView.Adapter<HotVideoHolder>
    {
        private List<HotVideo> mHotVideos;
        public HotVideoAdapter(List<HotVideo> videos)
        {
            mHotVideos = videos;
        }

        @Override
        public HotVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(WhatsHotActivity.this);
            View view = inflater.inflate(R.layout.list_item_hot_video, parent, false);
            return new HotVideoHolder(view);
        }

        @Override
        public void onBindViewHolder(HotVideoHolder holder, int position) {
            HotVideo video = mHotVideos.get(position);
            holder.bindHotVideo(video);
        }

        @Override
        public int getItemCount() {
            return mHotVideos.size();
        }
    }

    private void fetchWhatsHot()
    {
        ApiEndPoint endPoint = RetrofitClient.getApiEndpoint();
        Call<WhatsHot> call = endPoint.getWhatsHot(Constants.jsonHeader());
        call.enqueue(new Callback<WhatsHot>() {
            @Override
            public void onResponse(Call<WhatsHot> call, Response<WhatsHot> response) {
                if(response.code()==200)
                {
                    WhatsHot whatsHot = response.body();
                    processHotVideos(whatsHot);
                }
                else {
                    displayErrorMessage(response);
                }
            }

            @Override
            public void onFailure(Call<WhatsHot> call, Throwable t) {
                Toast.makeText(WhatsHotActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processHotVideos(WhatsHot whatsHot) {
        List<HotVideo> videos = whatsHot.getVideoList();
        findViewById(R.id.whats_hot_pb).setVisibility(View.GONE);
        if(videos==null)
        {
            displayErrorAndExit();
        }
        else if(videos.size()<1)
        {
            findViewById(R.id.no_vids_tv).setVisibility(View.VISIBLE);
        }
        else {
            mHotVideoAdapter = new HotVideoAdapter(videos);
            mRecyclerView.setAdapter(mHotVideoAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private String getTimeAgoString(String date)
    {
        int[] values = TimeConversionUtil.timeDifferencesValues(date);
        int qualifier = values[0];
        int time = values[1];
        if(qualifier==TimeConversionUtil.MINUTE_QUALIFIER)
        {
            if(time==1) return getResources().getString(R.string.just_now);
            return String.format(getResources()
                    .getQuantityString(R.plurals.minutes_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.HOUR_QUALIFIER)
        {
            return String.format(getResources().getQuantityString(R.plurals.hours_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.DAY_QUALIFIER)
        {
            return String.format(getResources().getQuantityString(R.plurals.days_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.WEEK_QUALIFIER)
        {
            return String.format(getResources().getQuantityString(R.plurals.weeks_ago, time), time);
        }
        else {
            return String.format(getResources().getQuantityString(R.plurals.years_ago, time), time);
        }
    }

    private void displayErrorMessage(Response response)
    {
        try {
            Log.d(TAG, String.valueOf(response.code()));
            JSONObject errorMessage = new JSONObject(response.errorBody().string());
            String errorString = errorMessage.getString("message");
            errorString = errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            displayErrorAndExit();
            e.printStackTrace();
        } catch (JSONException j)
        {
            displayErrorAndExit();
            j.printStackTrace();
        }
    }

    private void displayErrorAndExit() {
        Toast.makeText(this, R.string.unable_to_fetch_videos, Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED);
        finish();
    }
}
