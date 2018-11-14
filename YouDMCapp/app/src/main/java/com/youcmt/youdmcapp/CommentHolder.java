package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.Comment;
import com.youcmt.youdmcapp.model.RateRequest;
import com.youcmt.youdmcapp.model.RatingResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.youcmt.youdmcapp.Constants.ACCESS_TOKEN;
import static com.youcmt.youdmcapp.Constants.ID_GUEST;
import static com.youcmt.youdmcapp.Constants.USER_ID;

/**
 * Created by Stanislav Ostrovskii on 10/17/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * A ViewHolder to hold the view of a comment.
 */

public class CommentHolder extends RecyclerView.ViewHolder
{
    private static final String TAG = "CommentHolder";
    private Comment mComment;
    private Context mContext;
    private TextView mTimestamp;
    private TextView mUsername;
    private TextView mCommentBody;
    private TextView mRankTextView;
    private TextView mReplyCountTextView;

    private ImageView mUpButton;
    private ImageView mDownButton;
    private ImageView mReplyButton;
    private ImageView mCommentMenuButton;

    private SharedPreferences mPreferences;

    public CommentHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        mPreferences = mContext.getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        mUsername = itemView.findViewById(R.id.username);
        mCommentBody = itemView.findViewById(R.id.body);
        mTimestamp = itemView.findViewById(R.id.timestamp);
        mRankTextView = itemView.findViewById(R.id.rank);
        mReplyCountTextView = itemView.findViewById(R.id.reply_count);

        mUpButton = itemView.findViewById(R.id.up_button);
        mDownButton = itemView.findViewById(R.id.down_button);
        mReplyButton = itemView.findViewById(R.id.reply_button);
        mCommentMenuButton = itemView.findViewById(R.id.comment_menu_button);
    }

    private void setListeners() {
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateComment(true);
            }
        });
        mDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateComment(false);
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
        mCommentMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Button Clicked", Toast.LENGTH_LONG).show();
                showPopupMenu();
            }
        });

    }

    private void rateComment(boolean up) {
        if(mPreferences.getInt(USER_ID, ID_GUEST)==ID_GUEST)
        {
            Toast.makeText(mContext, "You must log in to rate comments!", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiEndPoint client = RetrofitClient.getApiEndpoint();

        RateRequest rateRequest = new RateRequest();
        rateRequest.setComment_id(mComment.getId());
        rateRequest.setRating(up ? 1 : -1);

        Call<RatingResponse> response = client.postRating(getAuthHeader(), rateRequest);
        response.enqueue(new Callback<RatingResponse>() {
            @Override
            public void onResponse(Call<RatingResponse> call, Response<RatingResponse> response) {
                Log.d(TAG, "Response code: " + response.code());

                if(response.code()==200)
                {
                    Toast.makeText(mContext, "Feedback posted!", Toast.LENGTH_SHORT).show();
                    int rating = response.body().getRating();
                    toggleThumbs(rating);
                    mRankTextView.setText(String.valueOf(mComment.getLike()-mComment.getDislike()+rating));
                }
                else if(response.code()==404)
                {
                    Toast.makeText(mContext, "Error code 404: Not Found", Toast.LENGTH_LONG).show();
                }
                else
                {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        String errorString = errorMessage.getString("message");
                        Toast.makeText(mContext, "Error code " +
                                response.code() + ": " + errorString, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        displayUnknownError();
                    } catch (NullPointerException n) {
                        n.printStackTrace();
                        displayUnknownError();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RatingResponse> call, Throwable t) {
                Toast.makeText(mContext, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleThumbs(int rating) {
       switch (rating)
       {
           case 0:
               mUpButton.setImageResource(R.drawable.ic_thumbs_up);
               mDownButton.setImageResource(R.drawable.ic_thumbs_down);
               break;
           case 1:
               mUpButton.setImageResource(R.drawable.ic_thumbs_up_selected);
               mDownButton.setImageResource(R.drawable.ic_thumbs_down);
               break;
           case -1:
               mUpButton.setImageResource(R.drawable.ic_thumbs_up);
               mDownButton.setImageResource(R.drawable.ic_thumbs_down_selected);
               break;
           default: displayUnknownError();
       }
    }

    private String getAuthHeader() {
        return "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(mContext, mCommentMenuButton);
        popupMenu.getMenuInflater().inflate(R.menu.comment_menu, popupMenu.getMenu());
        popupMenu.show();
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
        if(mComment.getCount()!=0)
        {
            mReplyCountTextView.setText(String.valueOf(mComment.getCount()));
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

    private void displayUnknownError() {
        Toast.makeText(mContext, "Unknown error occurred! Oops!", Toast.LENGTH_SHORT).show();
    }

}
