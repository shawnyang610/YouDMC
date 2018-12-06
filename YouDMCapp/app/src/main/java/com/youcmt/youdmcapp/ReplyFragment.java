package com.youcmt.youdmcapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.youcmt.youdmcapp.model.Comment;
import com.youcmt.youdmcapp.model.CommentResponse;
import com.youcmt.youdmcapp.model.ReplyPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.youcmt.youdmcapp.Constants.ACCESS_TOKEN;
import static com.youcmt.youdmcapp.Constants.ID_GUEST;
import static com.youcmt.youdmcapp.Constants.USER_ID;

/**
 * Created by Stanislav Ostrovskii on 10/25/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */
public class ReplyFragment extends Fragment  implements CommentHolder.FragmentCallbacks {
    private static final String COMMENT_KEY = "comment_key";
    private static final String TAG = "ReplyFragment";
    private View mFragmentView;
    private RecyclerView mRecyclerView;
    private ReplyAdapter mReplyAdapter;
    private Comment mComment;
    private EditText mCommentEditText;
    private List<Comment> mComments;
    private ImageView mSendButton;
    private SharedPreferences mPreferences;

    public static ReplyFragment newInstance(Comment comment) {
        ReplyFragment fragment = new ReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COMMENT_KEY, comment);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComment = (Comment) getArguments().getSerializable(COMMENT_KEY);
        mComments = new ArrayList<>(1);
        mPreferences = getActivity()
                .getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_comment_reply, container, false);

        ImageView exitButton = mFragmentView.findViewById(R.id.close_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //fetching the comment being replied to and placing it on the screen.
        View mainCommentView = mFragmentView.findViewById(R.id.the_comment);
        CommentHolder mainCommentHolder = new CommentHolder(mainCommentView, getActivity(), ReplyFragment.this);
        mainCommentHolder.bindComment(mComment);
        mainCommentHolder.hideReplyButton();
        mCommentEditText = mFragmentView.findViewById(R.id.new_reply_et);
        mSendButton = mFragmentView.findViewById(R.id.send_button);
        mCommentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0) mSendButton.setVisibility(VISIBLE);
                else mSendButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReply(mCommentEditText.getText().toString());
                mCommentEditText.setText("");
            }
        });
        return mFragmentView;
    }
    private void fetchComments()
    {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        retrofit2.Call<CommentResponse> call;
        if(mPreferences.getInt(USER_ID, ID_GUEST)==ID_GUEST) {
            call = client.loadReplies(String.valueOf(mComment.getId()), Constants.jsonHeader());
        } else {
            call = client.loadRepliesLoggedIn(getAuthHeader(), String.valueOf(mComment.getId()), Constants.jsonHeader());
        }
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CommentResponse> call, Response<CommentResponse> response) {

                if(response.code()==200) {
                    CommentResponse commentResponse = response.body();
                    processComments(commentResponse);
                }
                else if(response.code()==404)
                {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), errorMessage.getString("message"), Toast.LENGTH_SHORT).show();
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
                    displayUnknownError();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CommentResponse> call, Throwable t) {
                Log.d(TAG, "onFailureCalled "  + t.getMessage());
                displayUnknownError();
            }
        });
    }

    private void postReply(String commentText)
    {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();

        ReplyPostRequest postRequest = new ReplyPostRequest();
        postRequest.setText(commentText.trim());
        postRequest.setParent_comment_id(String.valueOf(mComment.getId()));

        Call<ResponseBody> response;
        if(mPreferences.getInt(USER_ID, ID_GUEST)==ID_GUEST)
        {
            response = client.postReplyGuest(postRequest, Constants.jsonHeader());
        }
        else {
            response = client.postReply(getAuthHeader(), postRequest, Constants.jsonHeader());

        }
        Log.d(TAG, "URL: " + response.request().url().toString());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "Response code: " + response.code());
                if(response.code()==200)
                {
                    Toast.makeText(getActivity(), "Comment posted!", Toast.LENGTH_SHORT).show();
                    if(mRecyclerView!=null) mRecyclerView.setVisibility(GONE);
                    mFragmentView.findViewById(R.id.comment_pb_reply).setVisibility(VISIBLE);
                    fetchComments(); //reload comment list

                }
                else {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        String errorString = errorMessage.getString("message");
                        errorString = errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
                        Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        displayUnknownError();
                    } catch (NullPointerException n) {
                        n.printStackTrace();
                        displayUnknownError();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayUnknownError();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @NonNull
    private String getAuthHeader() {
        return "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
    }

    private void displayUnknownError() {
        Toast.makeText(getActivity(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }

    private void processComments(CommentResponse response)
    {
        mRecyclerView = mFragmentView.findViewById(R.id.comment_rv_reply);
        mFragmentView.findViewById(R.id.comment_pb_reply).setVisibility(GONE);
        if(response.getCommentList()!=null){
            mComments.clear();
            for(Comment comment: response.getCommentList())
            {
                mComments.add(comment);
            }
            Log.d(TAG, "Fetched " + mComments.size() + " comments");
        }
        else displayUnknownError();

        if(mComments == null || mComments.size()==0)
        {
            mComments = new ArrayList<>(0);
            mRecyclerView.setVisibility(GONE);
            mFragmentView.findViewById(R.id.no_replies_tv).setVisibility(VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(VISIBLE);

            mFragmentView.findViewById(R.id.no_replies_tv).setVisibility(View.GONE);
            if(mReplyAdapter==null)
            {
                mReplyAdapter = new ReplyAdapter(mComments, getActivity());
                mRecyclerView.setAdapter(mReplyAdapter);
            }
            else {
                mReplyAdapter.notifyDataSetChanged();
            }
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchComments();
    }
  
    @Override
    public void finish() {
        Fragment fragment = new CommentFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_comment, fragment).commit();
    }
}
