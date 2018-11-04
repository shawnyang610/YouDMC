package com.youcmt.youdmcapp;

import android.os.Bundle;
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
import com.youcmt.youdmcapp.model.CommentPostRequest;
import com.youcmt.youdmcapp.model.CommentResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.youcmt.youdmcapp.Constants.ID_GUEST;
import static com.youcmt.youdmcapp.Constants.USER_ID;

/**
 * Created by Stanislav Ostrovskii on 10/25/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class ReplyFragment extends Fragment {
    private static final String COMMENT_KEY = "comment_key";
    private static final String TAG = "ReplyFragment";
    private int mUserId;
    private View mFragmentView;
    private RecyclerView mRecyclerView;
    private CommentAdapter mCommentAdapter;
    private CommentHolder mMainCommentHolder;
    private ImageView mExitButton;
    private Comment mComment;
    private EditText mCommentEditText;
    private List<Comment> mComments;
    private ImageView mSendButton;

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
        mUserId = getActivity().getPreferences(MODE_PRIVATE).getInt(USER_ID, ID_GUEST);
        mComments = new ArrayList<>(1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_comment_reply, container, false);

        mExitButton = mFragmentView.findViewById(R.id.close_button);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new CommentFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_comment, fragment).commit();
            }
        });
        //fetching the comment being replied to and placing it on the screen.
        View mainCommentView = mFragmentView.findViewById(R.id.the_comment);
        mMainCommentHolder = new CommentHolder(mainCommentView, getActivity());
        mMainCommentHolder.bindComment(mComment);
        mMainCommentHolder.hideReplyButton();
        mCommentEditText = mFragmentView.findViewById(R.id.new_reply_et);
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
                postReply(mCommentEditText.getText().toString());
                mCommentEditText.setText("");

            }
        });
        return mFragmentView;
    }
    private void fetchComments()
    {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        retrofit2.Call<CommentResponse> call = client.loadComments(String.valueOf(mComment.getId()), header());
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
                        Log.d(TAG, String.valueOf(response.code()));
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
                    Log.d(TAG, "Other Code");
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

        CommentPostRequest postRequest = new CommentPostRequest();
        postRequest.setText(commentText.trim());
        //missleading method name, I know, I know...
        postRequest.setVid(String.valueOf(mComment.getId()));
        postRequest.setUser_id(mUserId);

        retrofit2.Call<ResponseBody> response = client.postComment(postRequest, header());
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
                else if(response.code()==400)
                {
                    Toast.makeText(getActivity(), "Unable to process request!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Unknown error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayUnknownError() {
        Toast.makeText(getActivity(), "Unknown error occurred! Oops!", Toast.LENGTH_SHORT).show();
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

    private HashMap header()
    {
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        return header;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchComments();
    }
}
