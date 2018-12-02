package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.Comment;
import com.youcmt.youdmcapp.model.DeleteCommentRequest;
import com.youcmt.youdmcapp.model.LoginResponse;
import com.youcmt.youdmcapp.model.UpdateCommentRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.youcmt.youdmcapp.Constants.ACCESS_TOKEN;

/**
 * Created by Stanislav Ostrovskii on 10/17/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder>
implements CommentHolder.AdapterCallbacks {
    private static final String TAG = "CommentAdapter";
    private SharedPreferences mPreferences;

    protected List<Comment> mComments;
    protected Context mContext;

    /**
     * @param comments comments list from server.
     * @param context pass Activity context.
     */
    public CommentAdapter(List<Comment> comments, Context context)
    {
        mContext = context;
        mPreferences = mContext.getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        mComments = comments;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.comment, parent, false);
        return new CommentHolder(view, mContext, this);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.bindComment(comment);
        holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    @Override
    public void delete(final int position) {
        Comment commentToDelete = mComments.get(position);
        DeleteCommentRequest request =
                new DeleteCommentRequest(commentToDelete.getId());
        ApiEndPoint apiEndPoint = RetrofitClient.getApiEndpoint();
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");

        Call<ResponseBody> response = apiEndPoint.deleteComment("Bearer " + mPreferences.getString(ACCESS_TOKEN, ""), request, header);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Response code: " + response.code());
                if(response.code()==200) {
                    mComments.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "Comment deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        Toast.makeText(mContext, errorMessage.getString("message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, mContext.getResources().getString(R.string.server_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void update(final int position, final Comment comment) {
        Comment commentToUpdate = mComments.get(position);
        Log.d(TAG, "CommentToUpdate id: " + commentToUpdate.getId());
        UpdateCommentRequest request =
                new UpdateCommentRequest(commentToUpdate.getId(), comment.getText());
        ApiEndPoint apiEndPoint = RetrofitClient.getApiEndpoint();
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");

        Call<ResponseBody> response = apiEndPoint.updateComment("Bearer " + mPreferences.getString(ACCESS_TOKEN, ""), request, header);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Response code: " + response.code());
                if(response.code()==200) {
                    mComments.set(position, comment);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "Comment updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        Toast.makeText(mContext, errorMessage.getString("message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, mContext.getResources().getString(R.string.server_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUnknownError() {
        Toast.makeText(mContext, "Unknown error occurred! Oops!", Toast.LENGTH_SHORT).show();
    }
}
