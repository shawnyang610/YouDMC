package com.youcmt.youdmcapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youcmt.youdmcapp.model.Comment;

import java.util.List;

/**
 * Created by Stanislav Ostrovskii on 10/17/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * Like the CommentHolder, this is an independent class solely to keep VideoActivity
 * cleaner and more focused.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {
    private List<Comment> mComments;
    private Context mContext;

    /**
     * @param comments comments list from server.
     * @param context pass Activity context.
     */
    public CommentAdapter(List<Comment> comments, Context context)
    {
        mContext = context;
        mComments = comments;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.comment_top_level, parent, false);
        return new CommentHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.bindComment(comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }
}
