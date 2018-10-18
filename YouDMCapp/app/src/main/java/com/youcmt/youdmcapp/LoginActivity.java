package com.youcmt.youdmcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


public class LoginActivity extends AppCompatActivity implements LoginCallbacks {
    public static final String LOGGED_IN = "LoggedIn";
    private static final String TAG = "LoginActivity";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPreferences = getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        if(mPreferences.getBoolean(LOGGED_IN, false))
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

    @Override
    public void onSuccessfulLogin() {

        mEditor.putBoolean(LOGGED_IN, true).apply();
        startMainActivity();
    }

    public void onSuccessfulRegistration() {

        mEditor.putBoolean(LOGGED_IN, true).apply();
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
