package com.youcmt.youdmcapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


/**
 * Created by Stanislav Ostrovskii on 11/21/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * This fragment is the view where the registered user is able to edit
 * their comment.
 */

public class EditCommentFragment extends DialogFragment {

    private static final String ARG_BODY = "body";
    private static final String ARG_HOLDER = "holder";
    private EditText mEditText;
    private Callbacks mCallbacks;

    public static EditCommentFragment newInstance(String body, CommentHolder holder)
    {
        Bundle args = new Bundle();
        args.putString(ARG_BODY, body);
        args.putSerializable(ARG_HOLDER, holder);
        EditCommentFragment fragment = new EditCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCallbacks = (Callbacks) getArguments().getSerializable(ARG_HOLDER);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_comment, null);
        mEditText = view.findViewById(R.id.edit_comment_text);
        mEditText.setText(getArguments().getString(ARG_BODY));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditCommentFragment.this.dismiss();
            }
        });
        builder.setPositiveButton(getActivity().getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCallbacks.updateComment(mEditText.getText().toString());
            }
        });
        return builder.show();
    }

    /**
     * To get a communication channel with associated CommentHolder
     * CommentHolder implements this interface
     */
    public interface Callbacks
    {
        void updateComment(String text);
    }
}
