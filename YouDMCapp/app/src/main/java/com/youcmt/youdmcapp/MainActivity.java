package com.youcmt.youdmcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private TextView mTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.logout:
                Boolean success =
                        mPreferences.edit().putBoolean(LoginActivity.LOGGED_IN, false).commit();
                if(success) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(this, "Logout unsuccessful", Toast.LENGTH_SHORT);
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferences = getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        mTextView = findViewById(R.id.title_tv);
        setContentView(R.layout.activity_video);
    }

    /**
     * @param videoUrl pass the full url. The method will parse the URL.
     */
    private void getVideo(String videoUrl){
        try {
            Intent intent = FetchVideoService.newIntent(this, videoUrl);
            startService(intent);
        } catch (Exception e) {
            mTextView.setText(e.getMessage());
        }
    }
}
