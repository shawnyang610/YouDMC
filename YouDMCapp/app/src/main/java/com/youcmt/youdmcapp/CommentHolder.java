package com.youcmt.youdmcapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.youcmt.youdmcapp.model.Comment;

/**
 * Created by Stanislav Ostrovskii on 10/17/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * A ViewHolder to hold the view of a comment.
 */

public class CommentHolder extends RecyclerView.ViewHolder
{
    private Comment mComment;
    private Context mContext;
    private TextView mTimestamp;
    private TextView mUsername;
    private TextView mCommentBody;
    private TextView mRankTextView;

    private ImageView mUpButton;
    private ImageView mDownButton;
    private ImageView mReplyButton;

    public CommentHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        mUsername = itemView.findViewById(R.id.username);
        mCommentBody = itemView.findViewById(R.id.body);
        mTimestamp = itemView.findViewById(R.id.timestamp);
        mRankTextView = itemView.findViewById(R.id.rank);

        mUpButton = itemView.findViewById(R.id.up_button);
        mDownButton = itemView.findViewById(R.id.down_button);
        mReplyButton = itemView.findViewById(R.id.reply_button);
    }

    private void setListeners() {
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mComment.setLike(mComment.getLike()+1);
                mRankTextView.setText(String.valueOf(mComment.getLike()-mComment.getDislike()));
                mDownButton.setImageResource(R.drawable.ic_thumbs_down);
                mUpButton.setImageResource(R.drawable.ic_thumbs_up_selected);
            }
        });
        mDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mComment.setDislike(mComment.getDislike()+1);
                mRankTextView.setText(String.valueOf(mComment.getLike()-mComment.getDislike()));
                mDownButton.setImageResource(R.drawable.ic_thumbs_down_selected);
                mUpButton.setImageResource(R.drawable.ic_thumbs_up);
            }
        });
        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = ReplyFragment.newInstance(mComment);
                FragmentTransaction fragmentTransaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_comment, fragment).commit();
            }
        });
    }

    void bindComment(Comment comment)
    {
        mComment = comment;
        mUsername.setText(comment.getUsername());
        mCommentBody.setText(comment.getText());
        mTimestamp.setText(getTimeAgoString(comment.getDate()));
        int mLikeCount = mComment.getLike() - mComment.getDislike();
        if(mLikeCount!=0)
        {
            mRankTextView.setText(String.valueOf(mLikeCount));
        }
        setListeners();
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

    void hideReplyButton()
    {
        mReplyButton.setVisibility(View.GONE);
    }


}
