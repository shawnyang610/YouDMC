package com.youcmt.youdmcapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.User;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stanislav Ostrovskii on 9/19/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * This fragment allows a user to log in to the service with their credentials.
 */

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mForgotButton;
    private Button mLoginButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUsernameEditText = view.findViewById(R.id.username_et);
        mPasswordEditText = view.findViewById(R.id.password_et);
        mForgotButton = view.findViewById(R.id.forgot_password_button);
        mForgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPass();
            }
        });
        mLoginButton = view.findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //enable back button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }

    private void forgotPass() {
        Toast.makeText(getContext(), "Too bad!", Toast.LENGTH_SHORT).show();
    }

    private void login() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://youcmt.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        YouCmtClient client = retrofit.create(YouCmtClient.class);

        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        Call<User> response = client.login(username, header);

        Log.d(TAG, "URL: " + response.request().url().toString());

        response.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(response.code()==200) {
                    User user = response.body();
                     Log.d(TAG, user.getUsername());
                }
                else Log.d(TAG, String.valueOf(response.code()) + " ");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Failed");
            }
        });
    }


}
