package com.youcmt.youdmcapp;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.Comment;
import com.youcmt.youdmcapp.model.CommentPostRequest;
import com.youcmt.youdmcapp.model.CommentResponse;
import com.youcmt.youdmcapp.model.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.youcmt.youdmcapp.Constants.ACCESS_TOKEN;
import static com.youcmt.youdmcapp.Constants.EXTRA_VIDEO;
import static com.youcmt.youdmcapp.Constants.ID_GUEST;
import static com.youcmt.youdmcapp.Constants.USER_ID;

/**
 * Created by Stanislav Ostrovskii on 10/25/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class CommentFragment extends Fragment {
    private static final String TAG = "CommentFragment";
    private View mFragmentView;
    private TextView mTitleView;
    private RecyclerView mRecyclerView;
    private CommentAdapter mCommentAdapter;
    private EditText mCommentEditText;
    private ImageView mSendButton;
    private Video mVideo;
    private List<Comment> mComments;
    private SharedPreferences mPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComments = new ArrayList<>(1);
        mVideo = getActivity().getIntent().getParcelableExtra(EXTRA_VIDEO);
        mPreferences = getActivity()
                .getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_comment, container, false);
        mTitleView = mFragmentView.findViewById(R.id.title_tv);
        mTitleView.setText(mVideo.getTitle());
        mTitleView.setTextSize(getTextSize(mVideo.getTitle()));
        mTitleView.setOnClickListener(new View.OnClickListener() {
            private boolean mExpanded = false;
            private Drawable arrowUp = getResources()
                    .getDrawable(R.drawable.ic_arrow_drop_up);
            private Drawable arrowDown = getResources()
                    .getDrawable(R.drawable.ic_arrow_drop_down);
            TextView mDescriptionView = mFragmentView.findViewById(R.id.video_description);
            @Override
            public void onClick(View view) {
                if(mExpanded)
                {
                    mDescriptionView.setVisibility(GONE);
                    mTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDown, null);
                }
                else {
                    mDescriptionView.setVisibility(VISIBLE);
                    mDescriptionView.setText(mVideo.getDescription());
                    mTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowUp, null);
                }
                mExpanded = !mExpanded;
            }
        });
        mCommentEditText = mFragmentView.findViewById(R.id.new_comment_et);
        mSendButton = mFragmentView.findViewById(R.id.send_button);
        mCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0) mSendButton.setVisibility(VISIBLE);
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
        return mFragmentView;
    }

    private void fetchComments()
    {
        ApiEndPoint endPoint = RetrofitClient.getApiEndpoint();
        Call<CommentResponse> call;
        if(mPreferences.getInt(USER_ID, ID_GUEST)==ID_GUEST) {
            call = endPoint.loadComments(mVideo.getVid(), Constants.jsonHeader());
        } else {
            call = endPoint.loadCommentsLoggedIn(getAuthHeader(), mVideo.getVid(), Constants.jsonHeader());
        }
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.code() == 200) {
                    CommentResponse commentResponse = response.body();
                    processComments(commentResponse);
                } else {
                    displayErrorMessage(response);
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment(String commentText)
    {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();

        CommentPostRequest postRequest = new CommentPostRequest();
        postRequest.setText(commentText.trim());
        postRequest.setVid(mVideo.getVid());

        Call<ResponseBody> response;
        if(mPreferences.getInt(USER_ID, ID_GUEST)==ID_GUEST)
        {
            response = client.postCommentGuest(postRequest, Constants.jsonHeader());
        }
        else {
            response = client.postComment(getAuthHeader(), postRequest, Constants.jsonHeader());
        }
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.code()==200)
                {
                    Toast.makeText(getActivity(), R.string.comment_posted_toast, Toast.LENGTH_SHORT).show();
                    mRecyclerView.setVisibility(GONE);
                    mFragmentView.findViewById(R.id.comment_pb).setVisibility(VISIBLE);
                    fetchComments(); //reload comment list
                }
                else {
                    displayErrorMessage(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayUnknownError() {
        Toast.makeText(getActivity(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }

    private void displayErrorMessage(Response response)
    {
        try {
            Log.d(TAG, String.valueOf(response.code()));
            JSONObject errorMessage = new JSONObject(response.errorBody().string());
            String errorString = errorMessage.getString("message");
            errorString = errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
            Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            displayUnknownError();
            e.printStackTrace();
        } catch (JSONException j)
        {
            displayUnknownError();
            j.printStackTrace();
        }
    }

    private void processComments(CommentResponse response)
    {
        mRecyclerView = mFragmentView.findViewById(R.id.comment_rv);
        mFragmentView.findViewById(R.id.comment_pb).setVisibility(GONE);
        if(response.getCommentList()!=null){
            mComments.clear();
            mComments.addAll(response.getCommentList());
            for (Iterator<Comment> iterator = mComments.iterator(); iterator.hasNext(); ) {
                Comment value = iterator.next();
                if (value.getIs_deleted()==1) {
                    iterator.remove();
                }
            }
        }
        else displayUnknownError();

        if(mComments==null || mComments.size()==0)
        {
            mComments = new ArrayList<>(0);
            mRecyclerView.setVisibility(GONE);
            mFragmentView.findViewById(R.id.no_comments_tv).setVisibility(VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(VISIBLE);

            mFragmentView.findViewById(R.id.no_comments_tv).setVisibility(View.GONE);
            if(mCommentAdapter==null)
            {
                mCommentAdapter = new CommentAdapter(mComments, getActivity());
                mRecyclerView.setAdapter(mCommentAdapter);
            }
            else {
                mCommentAdapter.notifyDataSetChanged();
            }
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @NonNull
    private String getAuthHeader() {
        return "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
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
    public void onResume() {
        super.onResume();
        fetchComments();
    }
}
