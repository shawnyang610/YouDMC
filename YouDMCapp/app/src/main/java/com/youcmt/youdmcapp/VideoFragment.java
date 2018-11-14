package com.youcmt.youdmcapp;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Stanislav Ostrovskii on 10/28/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public abstract class VideoFragment extends Fragment {

    protected abstract void fetchComments();
    protected abstract void processComments();
    protected abstract void postComment();

    @Override
    public void onResume() {
        super.onResume();
        fetchComments();
    }

    protected HashMap header()
    {
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        return header;
    }

    protected void displayUnknownError() {
        Toast.makeText(getActivity(), "Unknown error occurred! Oops!", Toast.LENGTH_SHORT).show();
    }
}
