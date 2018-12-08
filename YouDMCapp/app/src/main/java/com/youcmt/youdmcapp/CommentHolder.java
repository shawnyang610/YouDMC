package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import com.squareup.picasso.Picasso;
import com.youcmt.youdmcapp.model.Comment;
import com.youcmt.youdmcapp.model.DeleteCommentRequest;
import com.youcmt.youdmcapp.model.RateRequest;
import com.youcmt.youdmcapp.model.RatingResponse;
import com.youcmt.youdmcapp.model.UpdateCommentRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import okhttp3.ResponseBody;

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
    //it should have one or the other, not both
    private AdapterCallbacks mAdapterCallbacks;
    private FragmentCallbacks mFragmentCallbacks;

    private Comment mComment;
    private AppCompatActivity mActivity; //host activity

    private ImageView mImageView;
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

    public CommentHolder(@NonNull View itemView, Context context, AdapterCallbacks adapter) {
        this(itemView, context);
        mAdapterCallbacks = adapter;
    }

    public CommentHolder(@NonNull View itemView, Context context, FragmentCallbacks fragment) {
        this(itemView, context);
        mFragmentCallbacks = fragment;
    }

    private CommentHolder(View itemView, Context context)
    {
        super(itemView);

        mActivity = (AppCompatActivity) context;
        mPreferences = mActivity.getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);

        mUsername = itemView.findViewById(R.id.username);
        mImageView = itemView.findViewById(R.id.avatar);
        mCommentBody = itemView.findViewById(R.id.body);
        mTimestamp = itemView.findViewById(R.id.timestamp);
        mRankTextView = itemView.findViewById(R.id.rank);
        mReplyCountTextView = itemView.findViewById(R.id.reply_count);
        mUpButton = itemView.findViewById(R.id.up_button);
        mDownButton = itemView.findViewById(R.id.down_button);
        mReplyButton = itemView.findViewById(R.id.reply_button);
        mCommentMenuButton = itemView.findViewById(R.id.comment_menu_button);
    }

    private void setImage() {
        if(mComment.getProfile_img()!=null) {
            String imgNumber = mComment.getProfile_img();
            String url = Constants.BASE_AVATAR_URL + imgNumber + ".png";
            Drawable placeholder = mActivity.getResources()
                    .getDrawable(R.drawable.default_avatar);
            Picasso.with(mActivity).load(url).placeholder(placeholder).into(mImageView);
        }
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
                createReplyFragment();
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
            Toast.makeText(mActivity, R.string.login_to_rate, Toast.LENGTH_SHORT).show();
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
                if(response.code()==200)
                {
                    Toast.makeText(mActivity, R.string.feedback_posted, Toast.LENGTH_SHORT).show();
                    int rating = response.body().getRating();
                    toggleThumbs(rating);
                    mRankTextView.setText(String.valueOf(mComment.getLike()-mComment.getDislike()+rating));
                }
                else if(response.code()==404)
                {
                    Toast.makeText(mActivity, R.string.error_404, Toast.LENGTH_LONG).show();
                }
                else
                {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        String errorString = errorMessage.getString("message");
                        Toast.makeText(mActivity, errorString, Toast.LENGTH_LONG).show();
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
        if(mAdapterCallbacks==null)
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
                        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                        EditCommentFragment fragment = EditCommentFragment.newInstance(mComment.getText(), CommentHolder.this);

                        fragment.show(fragmentManager, DIALOG_EDIT);
                        return true;
                    case R.id.flag_comment:
                        Toast.makeText(mActivity, "Reporting comments is against free speech", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.reply_comment:
                        createReplyFragment();
                        return true;
                    case  R.id.cancel_action:
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
                        if (mAdapterCallbacks != null) {
                            mAdapterCallbacks.delete(getAdapterPosition());
                        }
                        else if (mFragmentCallbacks != null)
                        {
                            delete();
                        }
                        else {
                            displayUnknownError();
                        }
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
        if(mComment.getVoted()==1)
        {
            toggleThumbs(1);
        }
        else if(mComment.getVoted()==-1)
        {
            toggleThumbs(-1);
        }
        setListeners();
        setImage();
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
        Toast.makeText(mActivity, R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateComment(String text) {
        mComment.setText(text);
        if(mAdapterCallbacks !=null) {
            mAdapterCallbacks.update(getAdapterPosition(), mComment);
        }
        else
        {
            //if it is the main comment in a reply fragment
            update();
        }
    }

    /** adapter holding this CommentHolder should implement this interface
     * for deleting and editing functionality to work
     */
    public interface AdapterCallbacks
    {
        void delete(int position);
        void update(int position, Comment comment);
    }

    /**
     * To communicate with the ReplyFragment if this CommentHolder is not within an Adapter
     */
    public interface FragmentCallbacks
    {
        void finish();
    }
    
    private void delete()
    {
        DeleteCommentRequest request =
                new DeleteCommentRequest(mComment.getId());
        ApiEndPoint apiEndPoint = RetrofitClient.getApiEndpoint();

        Call<ResponseBody> response = apiEndPoint.deleteComment("Bearer " + mPreferences.getString(ACCESS_TOKEN, ""), request, Constants.jsonHeader());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    Toast.makeText(mActivity, R.string.comment_deleted_toast, Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        Toast.makeText(mActivity, errorMessage.getString("message"), Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.server_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
        mFragmentCallbacks.finish();
    }
    
    private void update()
    {
        UpdateCommentRequest request =
                new UpdateCommentRequest(mComment.getId(), mComment.getText());
        ApiEndPoint apiEndPoint = RetrofitClient.getApiEndpoint();

        Call<ResponseBody> response = apiEndPoint.updateComment("Bearer " + mPreferences.getString(ACCESS_TOKEN, ""), request, Constants.jsonHeader());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    mCommentBody.setText(mComment.getText());
                    Toast.makeText(mActivity, R.string.comment_updated, Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        Toast.makeText(mActivity, errorMessage.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        displayUnknownError();
                        e.printStackTrace();
                    } catch (JSONException j)
                    {
                        displayUnknownError();
                        j.printStackTrace();
                      }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.server_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createReplyFragment()
    {
        Fragment fragment = ReplyFragment.newInstance(mComment);
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_comment, fragment).commit();
    }

}
