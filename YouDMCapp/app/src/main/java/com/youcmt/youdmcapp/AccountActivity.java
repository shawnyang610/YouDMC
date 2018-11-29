package com.youcmt.youdmcapp;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.UpdateProfileRequest;

import okhttp3.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.youcmt.youdmcapp.Constants.ACCESS_TOKEN;
import static com.youcmt.youdmcapp.Constants.USERNAME;

/**
 * Created by Stanislav Ostrovskii on 11/21/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * An activity where the user can change their e-mail, password,
 * and avatar.
 */
public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";
    private SharedPreferences mPreferences;
    private Fragment mAvatarFragment;

    private LinearLayout mPassLayout;
    private LinearLayout mEmailLayout;
    private FrameLayout mAvatarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mAvatarFragment = getSupportFragmentManager().findFragmentById(R.id.image_layout);
        mPreferences = getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        setTopText();
        Button expandPassButton = findViewById(R.id.expand_password_change);
        expandPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPassLayout.getVisibility()!=View.VISIBLE) {
                    mPassLayout.setVisibility(View.VISIBLE);
                    mEmailLayout.setVisibility(View.GONE);
                    mAvatarLayout.setVisibility(View.GONE);
                }
                else mPassLayout.setVisibility(View.GONE);
            }
        });
        Button expandEmailButton = findViewById(R.id.expand_email_change);
        expandEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEmailLayout.getVisibility()!=View.VISIBLE)
                {
                    mPassLayout.setVisibility(View.GONE);
                    mEmailLayout.setVisibility(View.VISIBLE);
                    mAvatarLayout.setVisibility(View.GONE);
                }
                else mEmailLayout.setVisibility(View.GONE);
            }
        });
        Button expandAvatarButton = findViewById(R.id.expand_image_change);
        expandAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAvatarLayout.getVisibility()!=View.VISIBLE)
                {
                    mPassLayout.setVisibility(View.GONE);
                    mEmailLayout.setVisibility(View.GONE);
                    mAvatarLayout.setVisibility(View.VISIBLE);
                    //avatar selection is done in a separate fragment for cleaner code
                    if(mAvatarFragment==null)
                    {
                        mAvatarFragment = new AvatarFragment();
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.image_layout, mAvatarFragment).commit();
                    }
                }
                else mAvatarLayout.setVisibility(View.GONE);
            }
        });

        mPassLayout = findViewById(R.id.password_layout);
        mEmailLayout = findViewById(R.id.email_layout);
        mAvatarLayout = findViewById(R.id.image_layout);

        Button setNewPass = findViewById(R.id.submit_pass_button);
        setNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currPassEntry =
                        ((EditText) findViewById(R.id.current_password_et))
                                .getText().toString();
                String newPass1 = ((EditText) findViewById(R.id.new_pass_et))
                        .getText().toString();
                String newPass2 = ((EditText) findViewById(R.id.new_pass_et2))
                        .getText().toString();
                if(!ValidationUtils.isPasswordValid(newPass1))
                {
                    Toast.makeText(AccountActivity.this, R.string.weak_password_error, Toast.LENGTH_SHORT).show();
                }
                else if(!ValidationUtils.doPasswordsMatch(newPass1, newPass2))
                {
                    Toast.makeText(AccountActivity.this, R.string.mismatched_passwords_error, Toast.LENGTH_SHORT).show();
                }
                else if(ValidationUtils.doPasswordsMatch(currPassEntry, newPass1))
                {
                    Toast.makeText(AccountActivity.this, R.string.same_passwords_error, Toast.LENGTH_SHORT).show();
                }
                else if(!ValidationUtils.isPasswordValid(currPassEntry))
                {
                    Toast.makeText(AccountActivity.this, "" + currPassEntry, Toast.LENGTH_SHORT).show();
                    Toast.makeText(AccountActivity.this, R.string.password_incorrect, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    UpdateProfileRequest request = new UpdateProfileRequest();
                    request.setOld_password(currPassEntry);
                    request.setNew_password(newPass1);
                    tryUpdatingProfile(request);
                }
            }
        });
        Button setNewEmail = findViewById(R.id.submit_email_button);
        setNewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currPassEntry =
                        ((EditText) findViewById(R.id.current_password_et_email))
                                .getText().toString();
                String newEmail1 = ((EditText) findViewById(R.id.new_email_et))
                        .getText().toString();
                String newEmail2 = ((EditText) findViewById(R.id.new_email_et2))
                        .getText().toString();
                if(!ValidationUtils.isEmailAddressValid(newEmail1))
                {
                    Toast.makeText(AccountActivity.this, R.string.invalid_email_error, Toast.LENGTH_SHORT).show();
                }
                else if(!ValidationUtils.doPasswordsMatch(newEmail1, newEmail2))
                {
                    Toast.makeText(AccountActivity.this, R.string.mismatched_emails_error, Toast.LENGTH_SHORT).show();
                }
                else if(!ValidationUtils.isPasswordValid(currPassEntry))
                {
                    Toast.makeText(AccountActivity.this, R.string.password_incorrect, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    UpdateProfileRequest request  = new UpdateProfileRequest();
                    request.setOld_password(currPassEntry);
                    request.setNew_email(newEmail1);
                    tryUpdatingProfile(request);
                }
            }
        });
    }

    private void tryUpdatingProfile(UpdateProfileRequest request) {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        Call<ResponseBody> response = client.updateProfile(getAuthHeader(), request, header());
        Log.d(TAG, "URL: " + response.request().url().toString());

        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==200) {
                    Toast.makeText(AccountActivity.this,  R.string.update_success, Toast.LENGTH_SHORT).show();
                }
                else {
                    displayErrorMessage(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountActivity.this, "Network error! Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HashMap<String,String> header() {
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        return header;
    }

    @NonNull
    private String getAuthHeader() {
        return "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
    }


    private void displayErrorMessage(Response<ResponseBody> response) {
        try {
            Log.d(TAG, String.valueOf(response.code()));
            JSONObject errorMessage = new JSONObject(response.errorBody().string());
            String errorString = errorMessage.getString("message");
            errorString = errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            displayUnknownError();
            e.printStackTrace();
        } catch (JSONException j)
        {
            displayUnknownError();
            j.printStackTrace();
        }
    }

    private void displayUnknownError() {
        Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }

    private void setTopText()
    {
        String label = String.format(getString(R.string.account_label),
                mPreferences.getString(USERNAME, getString(R.string.error)));
        TextView topTextView = findViewById(R.id.account_tv);
        topTextView.setText(label);
        if(label.equals(getString(R.string.error))
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            topTextView.setTextColor(getColor(R.color.error_color));
    }
}
