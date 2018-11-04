package com.youcmt.youdmcapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.youcmt.youdmcapp.model.Comment;

/**
 * Created by Stanislav Ostrovskii on 10/17/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * A ViewHolder to hold the view of a comment.
 * Defined in an independent class to keep VideoActivity cleaner.
 */

public class CommentHolder extends RecyclerView.ViewHolder {
    private Context mContext;
    private TextView mTimestamp;
    private TextView mUsername;
    private TextView mCommentBody;

    public CommentHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        mUsername = itemView.findViewById(R.id.username);
        mCommentBody = itemView.findViewById(R.id.body);
        mTimestamp = itemView.findViewById(R.id.timestamp);
    }

    public void bindComment(Comment comment)
    {
        mUsername.setText(comment.getUsername());
        mCommentBody.setText(comment.getText());
        mTimestamp.setText(getTimeAgoString(comment.getDate()));
    }
    private String getTimeAgoString(String date)
    {
        int[] values = TimeConversionUtil.timeDifferencesValues(date);
        int qualifier = values[0];
        int time = values[1];
        if(qualifier==TimeConversionUtil.MINUTE_QUALIFIER)
        {
            if(time==1) return mContext.getResources().getString(R.string.just_now);
            return String.format(mContext.getResources()
                    .getQuantityString(R.plurals.minutes_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.HOUR_QUALIFIER)
        {
            return String.format(mContext.getResources().getQuantityString(R.plurals.hours_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.DAY_QUALIFIER)
        {
            return String.format(mContext.getResources().getQuantityString(R.plurals.days_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.WEEK_QUALIFIER)
        {
            return String.format(mContext.getResources().getQuantityString(R.plurals.weeks_ago, time), time);
        }
        else {
            return String.format(mContext.getResources().getQuantityString(R.plurals.years_ago, time), time);
        }
    }
}
