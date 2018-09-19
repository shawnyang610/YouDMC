package com.youcmt.youdmcapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.User;
import com.youcmt.youdmcapp.model.UserRequest;
import com.youcmt.youdmcapp.model.VideoRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stanislav Ostrovskii on 9/19/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
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
        return view;
    }

    private void forgotPass() {
        Toast.makeText(getContext(), "Too bad!", Toast.LENGTH_SHORT).show();
    }

    private void login() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        Log.i(TAG, "Username: " + username + " Password: " + password);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://youcmt.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        YouCmtClient client = retrofit.create(YouCmtClient.class);
        Call<User> response = client.login(username, password);
        response.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }


}
