package com.youcmt.youdmcapp;

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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stanislav Ostrovskii on 9/19/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mReenterEditText;
    private Button mRegisterButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mUsernameEditText = view.findViewById(R.id.username_et);
        mEmailEditText = view.findViewById(R.id.email_et);
        mPasswordEditText = view.findViewById(R.id.password_et);
        mReenterEditText = view.findViewById(R.id.password_reenter_et);
        mRegisterButton = view.findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAttempt();
            }
        });

        //enable back button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }

    private void registerAttempt() {
        String username = mUsernameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String password1 = mPasswordEditText.getText().toString();
        String password2 = mReenterEditText.getText().toString();
        if(username.length()<3)
        {
            Toast.makeText(getActivity(), "Select a longer username.", Toast.LENGTH_SHORT).show();
        }
        else if(!isEmailAddressValid(email)) {
            Toast.makeText(getActivity(), "Not a valid e-mail address", Toast.LENGTH_SHORT).show();
        }
        else if(!doPasswordsMatch(password1, password2))
        {
            Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else if(!isPasswordValid(password1))
        {
            Toast.makeText(getActivity(), "Choose a stronger password!", Toast.LENGTH_SHORT).show();
        }
        else registerProcess(username, email, password1);
    }

    /**
     * @param email
     * @return false if e-mail address entered is not well-formed
     * True does not guarantee a valid e-mail address
     */
    private static boolean isEmailAddressValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    /**
     * @param password
     * @return returns false is password is too short or does not contain one
     * lowercase or uppercase letter and one number. Otherwise returns true.
     */
    private static boolean isPasswordValid(String password)
    {
        if(password.length()<6) return false;

        //check to make sure password has one letter and one number
        boolean hasNum = false;
        boolean hasLetter = false;
        for(char c: password.toCharArray())
        {
            if((c>64 && c<91)||(c>96 && c<123))
                hasLetter = true;
            if(c>47 && c<58)
                hasNum = true;
        }
        if(!hasLetter||!hasNum) {
            return false;
        }
        return true;
    }

    /**
     * checks to see if two strings are equal.
     * @param one first string
     * @param two second string
     * @return true if they are equal, false otherwise.
     */
    private static boolean doPasswordsMatch(String one, String two)
    {
        return (one.equals(two));
    }

    /**
     * Attempts to register a user with the credentials entered.
     * @param username
     * @param email
     * @param password
     */
    private void registerProcess(String username, String email, String password)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://youcmt.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        YouCmtClient youCmtClient = retrofit.create(YouCmtClient.class);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");

        Call<ResponseBody> response = youCmtClient.registerUser(user, header);
        Log.d(TAG, "URL: " + response.request().url().toString());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.code()==201)
                {
                    login();
                }
                else if(response.code()==400)
                {
                    Toast.makeText(getActivity(), "Username or email taken!", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getActivity(), "Unknown error occurred!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login() {

    }
}
