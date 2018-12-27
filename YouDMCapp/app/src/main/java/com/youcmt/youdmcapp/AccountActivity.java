package com.youcmt.youdmcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import static com.youcmt.youdmcapp.Constants.ID_NONE;
import static com.youcmt.youdmcapp.Constants.LOGGED_IN;
import static com.youcmt.youdmcapp.Constants.REFRESH_TOKEN;
import static com.youcmt.youdmcapp.Constants.USERNAME;
import static com.youcmt.youdmcapp.Constants.USER_ID;

/**
 * Created by Stanislav Ostrovskii on 11/21/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * An activity that allows the user to change their e-mail, password,
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
        final Drawable arrowUp = getResources()
                .getDrawable(R.drawable.ic_arrow_drop_up);
        final Drawable arrowDown = getResources()
                .getDrawable(R.drawable.ic_arrow_drop_down);
        mAvatarFragment = getSupportFragmentManager().findFragmentById(R.id.image_layout);
        mPreferences = getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        setTopText();

        final Button expandPassButton = findViewById(R.id.expand_password_change);
        expandPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPassLayout.getVisibility()!=View.VISIBLE) {
                    mPassLayout.setVisibility(View.VISIBLE);
                    mEmailLayout.setVisibility(View.GONE);
                    mAvatarLayout.setVisibility(View.GONE);
                    expandPassButton.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowUp, null);

                }
                else
                {
                    expandPassButton.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDown, null);
                    mPassLayout.setVisibility(View.GONE);
                }
            }
        });
        final Button expandEmailButton = findViewById(R.id.expand_email_change);
        expandEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEmailLayout.getVisibility()!=View.VISIBLE)
                {
                    mPassLayout.setVisibility(View.GONE);
                    mEmailLayout.setVisibility(View.VISIBLE);
                    mAvatarLayout.setVisibility(View.GONE);
                    expandEmailButton.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowUp, null);
                }
                else
                {
                    expandEmailButton.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDown, null);
                    mEmailLayout.setVisibility(View.GONE);
                }
            }
        });
        final Button expandAvatarButton = findViewById(R.id.expand_image_change);
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
                    expandAvatarButton.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowUp, null);
                }
                else
                {
                    mAvatarLayout.setVisibility(View.GONE);
                    expandAvatarButton.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDown, null);
                }
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
        checkToken();
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        Call<ResponseBody> response = client.updateProfile(getAuthHeader(), request, Constants.jsonHeader());

        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==200) {
                    Toast.makeText(AccountActivity.this,  R.string.update_success, Toast.LENGTH_SHORT).show();
                    clearForm((ViewGroup) findViewById(R.id.password_layout));
                    clearForm((ViewGroup) findViewById(R.id.email_layout));
                }
                else {
                    try {
                        JSONObject errorMessage = new JSONObject(response.errorBody().string());

                        //the API seems to be inconsistent with error messages.
                        //so we try to extract both "msg" and "message"
                        //and give up only if both are null
                        String errorString = errorMessage.getString("msg");
                        if(errorString==null) errorString = errorMessage.getString("message");

                        errorString = errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
                        if(errorString.equalsIgnoreCase("Fresh token required"))
                        {
                            Toast.makeText(AccountActivity.this, R.string.login_again, Toast.LENGTH_SHORT).show();
                            logoutUser();
                        }
                        else Toast.makeText(AccountActivity.this, errorString, Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        displayUnknownError();
                        e.printStackTrace();
                    } catch (JSONException j)
                    {
                        displayUnknownError();
                        j.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logoutUser() {
        revokeTokens();
        Boolean success =
                mPreferences.edit()
                        .putBoolean(LOGGED_IN, false)
                        .putInt(USER_ID, ID_NONE)
                        .putString(ACCESS_TOKEN, null)
                        .putString(REFRESH_TOKEN, null)
                        .commit();
        if (success) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private String getAuthHeader() {
        return "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
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

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

    private void revokeTokens() {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        Call<ResponseBody> callAccess = client.logoutAccess("Bearer " + mPreferences.getString(ACCESS_TOKEN, ""));
        Call<ResponseBody> callRefresh = client.logoutRefresh("Bearer " + mPreferences.getString(REFRESH_TOKEN, ""));

        callAccess.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()!=200) {
                    try {
                        Toast.makeText(AccountActivity.this,
                                response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(AccountActivity.this, R.string.unknown_error, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountActivity.this, R.string.security_error, Toast.LENGTH_LONG).show();
            }
        });
        callRefresh.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()!=200) {
                    try {
                        Toast.makeText(AccountActivity.this, "Error code "
                                + response.code() + ": " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(AccountActivity.this, R.string.security_error, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkToken() {
        String token = "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        Call<ResponseBody> call = client.checkToken(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==200) {
                    return;
                }
                else  {
                    refreshAccessToken();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                refreshAccessToken();
            }
        });
    }

    private void refreshAccessToken() {
        String token = "Bearer " + mPreferences.getString(REFRESH_TOKEN, "");
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        Call<ResponseBody> call = client.refreshToken(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200) {
                    try {
                        JSONObject message = new JSONObject(response.body().string());
                        mPreferences.edit().putString(ACCESS_TOKEN, message.getString("access_token")).apply();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else  {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(AccountActivity.this, jObjError.getString("msg"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AccountActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
