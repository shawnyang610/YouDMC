package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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
import java.io.Serializable;

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
        implements EditCommentFragment.Callbacks, Serializable
{
    private static final String TAG = "CommentHolder";
    private static final String DIALOG_EDIT = "DialogEdit";
    private View mView;
    private AdapterCallbacks mCallbacks;
    private Comment mComment;
    private AppCompatActivity mActivity; //host activity
    private TextView mTimestamp;
    private TextView mUsername;
    private TextView mCommentBody;
    private TextView mRankTextView;
    private TextView mReplyCountTextView;

    private ImageView mUpButton;
    private ImageView mDownButton;
    private ImageView mReplyButton;
    private ImageView mCommentMenuButton;
    private RecyclerView.Adapter mAdapter; //the ViewHolders adapter

    private SharedPreferences mPreferences;

    public CommentHolder(@NonNull View itemView, Context context, RecyclerView.Adapter adapter) {
        super(itemView);
        mView = itemView;
        mAdapter = adapter;
        mCallbacks = (AdapterCallbacks) mAdapter;
        mActivity = (AppCompatActivity) context;
        mPreferences = mActivity.getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
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
                FragmentTransaction fragmentTransaction = ((AppCompatActivity) mActivity).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_comment, fragment).commit();
            }
        });
        mCommentMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu();
            }
        });

    }

    private void rateComment(boolean up) {
        if(mPreferences.getInt(USER_ID, ID_GUEST)==ID_GUEST)
        {
            Toast.makeText(mActivity, "You must log in to rate comments!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mActivity, "Feedback posted!", Toast.LENGTH_SHORT).show();
                    int rating = response.body().getRating();
                    toggleThumbs(rating);
                    mRankTextView.setText(String.valueOf(mComment.getLike()-mComment.getDislike()+rating));
                }
                else if(response.code()==404)
                {
                    Toast.makeText(mActivity, "Error code 404: Not Found", Toast.LENGTH_LONG).show();
                }
                else
                {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        String errorString = errorMessage.getString("message");
                        Toast.makeText(mActivity, "Error code " +
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
                Toast.makeText(mActivity, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
        PopupMenu popupMenu = new PopupMenu(mActivity, mCommentMenuButton);
        popupMenu.getMenuInflater().inflate(R.menu.comment_menu, popupMenu.getMenu());
        if(mAdapter==null)
        {
            popupMenu.getMenu().findItem(R.id.reply_comment).setVisible(false);
        }
        if(mComment.getUser_id() == mPreferences.getInt(USER_ID, ID_GUEST)
                && mPreferences.getInt(USER_ID, ID_GUEST)!=ID_GUEST) {
                popupMenu.getMenu().findItem(R.id.flag_comment).setVisible(false);
        }
        else {
            popupMenu.getMenu().findItem(R.id.delete_comment).setVisible(false);
            popupMenu.getMenu().findItem(R.id.edit_comment).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.delete_comment:
                        displayDeleteAlertDialog();
                        return true;
                    case R.id.edit_comment:
                        Log.d(TAG, "Edit clicked");
                        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                        EditCommentFragment fragment = EditCommentFragment.newInstance(mComment.getText(), CommentHolder.this);

                        fragment.show(fragmentManager, DIALOG_EDIT);
                        return true;
                    default: return false;
                }
            }
        });
        popupMenu.show();
    }

    private void displayDeleteAlertDialog() {
        AlertDialog.Builder builder;
        //aesthetics
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mActivity);
        }
        builder.setTitle(R.string.delete_comment_title)
                .setMessage(R.string.delete_comment_prompt)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCallbacks.delete(getAdapterPosition());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
            if(time==1) return mActivity.getResources().getString(R.string.just_now);
            return String.format(mActivity.getResources()
                    .getQuantityString(R.plurals.minutes_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.HOUR_QUALIFIER)
        {
            return String.format(mActivity.getResources().getQuantityString(R.plurals.hours_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.DAY_QUALIFIER)
        {
            return String.format(mActivity.getResources().getQuantityString(R.plurals.days_ago, time), time);
        }
        else if(qualifier==TimeConversionUtil.WEEK_QUALIFIER)
        {
            return String.format(mActivity.getResources().getQuantityString(R.plurals.weeks_ago, time), time);
        }
        else {
            return String.format(mActivity.getResources().getQuantityString(R.plurals.years_ago, time), time);
        }
    }

    void hideReplyButton()
    {
        mReplyButton.setVisibility(View.GONE);
        mReplyCountTextView.setVisibility(View.GONE);
    }

    private void displayUnknownError() {
        Toast.makeText(mActivity, "Unknown error occurred! Oops!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateComment(String text) {
        Toast.makeText(mActivity, "Updated comment", Toast.LENGTH_SHORT).show();
        mComment.setText(text);
        if(mCallbacks!=null)
            mCallbacks.update(getAdapterPosition(), mComment);
        else  mCommentBody.setText(text);
    }

    /** adapter holding this CommentHolder should implement this interface
     * for deleting and editing functionality to work
     */
    public interface AdapterCallbacks
    {
        void delete(int position);
        void update(int position, Comment comment);
    }
}
