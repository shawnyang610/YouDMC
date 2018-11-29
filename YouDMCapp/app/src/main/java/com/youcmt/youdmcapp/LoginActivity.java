package com.youcmt.youdmcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.youcmt.youdmcapp.model.LoginResponse;

import static com.youcmt.youdmcapp.Constants.ACCESS_TOKEN;
import static com.youcmt.youdmcapp.Constants.ID_GUEST;
import static com.youcmt.youdmcapp.Constants.LOGGED_IN;
import static com.youcmt.youdmcapp.Constants.PROFILE_IMG;
import static com.youcmt.youdmcapp.Constants.REFRESH_TOKEN;
import static com.youcmt.youdmcapp.Constants.USERNAME;
import static com.youcmt.youdmcapp.Constants.USER_ID;


public class LoginActivity extends AppCompatActivity implements LoginCallbacks {
    private static final String TAG = "LoginActivity";
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences preferences = getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        mEditor = preferences.edit();
        if(preferences.getBoolean(LOGGED_IN, false))
        {
            startMainActivity();
        }
        displayFragment();
    }

    private void displayFragment()
    {
        Fragment fragment;
        fragment = new SelectionFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * This ensures that instead of returning to the homescreen,
     * pressing the back button will return the previous fragment accessed.
     */
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO these methods seem the same for all intents and purposes. Merge?
    @Override
    public void onSuccessfulLogin(LoginResponse response) {
        mEditor.putInt(USER_ID, response.getId()).apply();
        mEditor.putString(USERNAME, response.getUsername());
        mEditor.putBoolean(LOGGED_IN, true).apply();
        mEditor.putString(ACCESS_TOKEN, response.getAccess_token()).apply();
        mEditor.putString(REFRESH_TOKEN, response.getRefresh_token()).apply();
        mEditor.putString(PROFILE_IMG, response.getProfile_img());
        startMainActivity();
        finish();
    }

    public void onSuccessfulRegistration(LoginResponse response) {
        mEditor.putInt(USER_ID, response.getId()).apply();
        mEditor.putString(USERNAME, response.getUsername());
        mEditor.putBoolean(LOGGED_IN, true).apply();
        mEditor.putString(ACCESS_TOKEN, response.getAccess_token()).apply();
        mEditor.putString(REFRESH_TOKEN, response.getRefresh_token()).apply();
        mEditor.putString(PROFILE_IMG, response.getProfile_img());
        startMainActivity();
        finish();
    }

    @Override
    public void onGuestLogin() {
        mEditor.putInt(USER_ID, ID_GUEST).apply();
        mEditor.putBoolean(LOGGED_IN, true).apply();
        mEditor.putString(ACCESS_TOKEN, null);
        mEditor.putString(REFRESH_TOKEN, null);
        startMainActivity();
        finish();
    }

    /**
     * Starts MainActivity, as the user is considered logged in.
     */
    private void startMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
