package com.youcmt.youdmcapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youcmt.youdmcapp.model.Comment;

import java.util.List;

/**
 * Created by Stanislav Ostrovskii on 11/10/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * TODO:
 * Currently, this class exists only to hide comment reply button on reply comments.
 * Perhaps in the future it will have more functionality.
 */

public class ReplyAdapter extends CommentAdapter {
    /**
     * @param comments comments list from server.
     * @param context  pass Activity context.
     */
    ReplyAdapter(List<Comment> comments, Context context) {
        super(comments, context);
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.comment, parent, false);
        view.findViewById(R.id.reply_button).setVisibility(View.GONE);
        return new CommentHolder(view, mContext, this);
    }
}
