package com.youcmt.youdmcapp;

import android.content.Context;
import android.content.res.Resources;
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

import com.youcmt.youdmcapp.model.LoginResponse;
import com.youcmt.youdmcapp.model.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import retrofit2.Call;
import retrofit2.Callback;

import static com.youcmt.youdmcapp.ValidationUtils.doPasswordsMatch;
import static com.youcmt.youdmcapp.ValidationUtils.isEmailAddressValid;
import static com.youcmt.youdmcapp.ValidationUtils.isPasswordValid;

/**
 * Created by Stanislav Ostrovskii on 9/19/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * This fragment contains the fields a user needs to enter to create an account
 * as well as performs validation checks on them.
 */

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mReenterEditText;
    private Button mRegisterButton;
    private LoginCallbacks mHostingActivity;
    private Resources mResources;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHostingActivity = (LoginCallbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mResources = getActivity().getResources();
        super.onCreate(savedInstanceState);
    }

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
            Toast.makeText(getActivity(), mResources.getString(R.string.longer_username_error), Toast.LENGTH_SHORT).show();
        }
        else if(!isEmailAddressValid(email)) {
            Toast.makeText(getActivity(), mResources.getString(R.string.invalid_email_error), Toast.LENGTH_SHORT).show();
        }
        else if(!doPasswordsMatch(password1, password2))
        {
            Toast.makeText(getActivity(), mResources.getString(R.string.mismatched_passwords_error), Toast.LENGTH_SHORT).show();
        }
        else if(!isPasswordValid(password1))
        {
            Toast.makeText(getActivity(), mResources.getString(R.string.weak_password_error), Toast.LENGTH_SHORT).show();
        }
        else registerProcess(username, email, password1);
    }

    /**
     * Attempts to register a user with the credentials entered.
     * @param username
     * @param email
     * @param password
     */
    private void registerProcess(String username, String email, String password)
    {
        ApiEndPoint apiEndPoint = RetrofitClient.getApiEndpoint();
        RegisterRequest user = new RegisterRequest(username, password, email);

        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");

        Call<LoginResponse> response = apiEndPoint.registerUser(user, header);
        response.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if(response.code()==201)
                {
                    LoginResponse loginResponse = response.body();
                    mHostingActivity.onSuccessfulRegistration(loginResponse);
                }
                else
                {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());
                        String errorString = errorMessage.getString("message");
                        errorString = errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
                        Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        displayUnknownError();
                    } catch (NullPointerException n) {
                        n.printStackTrace();
                        displayUnknownError();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayUnknownError();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayUnknownError() {
        Toast.makeText(getActivity(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }
}
