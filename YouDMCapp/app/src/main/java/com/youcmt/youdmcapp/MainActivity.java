package com.youcmt.youdmcapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.Video;

import static com.youcmt.youdmcapp.Constants.*;
import static com.youcmt.youdmcapp.FetchVideoService.FAIL;
import static com.youcmt.youdmcapp.FetchVideoService.SUCCESS;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ResponseReceiver mReceiver;
    private SharedPreferences mPreferences;
    private EditText mUrlEditText;
    private Button mSearchButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getType()!=null &&
                intent.getType().equals("text/plain")) {
            setIntent(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_main);
        mPreferences = getSharedPreferences("com.youcmt.youdmcapp", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        mUrlEditText = findViewById(R.id.url_search_et);
        mProgressBar = findViewById(R.id.progress_bar);
        mSearchButton = findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    askForVideo(mUrlEditText.getText().toString());
                    mSearchButton.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    mUrlEditText.setText("");
                    mUrlEditText.setHint(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //register the ResponseReceiver
        mReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FETCH_VIDEO_INFO);
        registerReceiver(mReceiver, intentFilter);

        Intent intent = getIntent();
        if (intent.getType()!=null &&
                intent.getType().equals("text/plain")) {
            Bundle extras = getIntent().getExtras();
            String value = extras.getString(Intent.EXTRA_TEXT);
            if(value!=null)
            {
                mUrlEditText.setText(value);
            }
        }
    }

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
                    Toast.makeText(this,
                            "Logout unsuccessful. Oops.", Toast.LENGTH_SHORT).show();
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @param url pass the full url. The method will parse the URL.
     */
    private void askForVideo (String url) throws Exception {
        url = url.trim();
        if(url.contains("="))
        {
            String[] parts = url.split("=");
            url = parts[1];
        }
        else if(url.contains(".be"))
        {
            String[] parts = url.split("/");
            url = parts[3];
        }
        else{
            throw new Exception("Not a valid youtube URL.");
        }

        try {
            Intent intent = FetchVideoService.newIntent(this, url);
            startService(intent);
        } catch (Exception e) {
            mUrlEditText.setText(e.getMessage());
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(EXTRA_VIDEO_STATUS, FAIL);
            mProgressBar.setVisibility(View.INVISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
            if(status==FAIL)
            {
                mUrlEditText.setText("");
                mUrlEditText.setHint("Error retrieving video!");
            }
            else if(status==SUCCESS) {
                Video video = intent.getParcelableExtra(EXTRA_VIDEO);
                Intent videoActivityIntent = VideoActivity.newIntent(getApplicationContext(), video);
                startActivity(videoActivityIntent);
            }
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }
}
