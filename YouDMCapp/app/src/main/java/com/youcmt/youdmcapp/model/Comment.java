package com.youcmt.youdmcapp.model;

import java.io.Serializable;

/**
 * Created by Stanislav Ostrovskii on 10/17/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * A POJO to represent a comment retrieved from the web server.
 */

public class Comment implements Serializable {
    private int id;
    private String date;
    private String edit_date;
    private int is_deleted;
    private String text;
    private int user_id;
    private String username;
    private String profile_img;
    private int top_comment_id;
    private int parent_comment_id;
    private int like;
    private int dislike;
    private int count;
    private int voted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTop_comment_id() {
        return top_comment_id;
    }

    public void setTop_comment_id(int top_comment_id) {
        this.top_comment_id = top_comment_id;
    }

    public int getParent_comment_id() {
        return parent_comment_id;
    }

    public void setParent_comment_id(int parent_comment_id) {
        this.parent_comment_id = parent_comment_id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public String getEdit_date() {
        return edit_date;
    }

    public boolean isEdited()
    {
        return getEdit_date().equals(getDate());
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public int getVoted() {
        return voted;
    }
}
