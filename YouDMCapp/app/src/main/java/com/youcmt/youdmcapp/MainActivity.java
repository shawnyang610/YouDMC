package com.youcmt.youdmcapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.mail.imap.protocol.ID;
import com.youcmt.youdmcapp.model.Video;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.youcmt.youdmcapp.Constants.*;
import static com.youcmt.youdmcapp.FetchVideoService.FAIL;
import static com.youcmt.youdmcapp.FetchVideoService.SUCCESS;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 * The application's main activity, featuring a URL entry search bar
 * and a menu with options.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_HOT_VIDEO = 0;
    private ResponseReceiver mReceiver;
    private boolean mIsReceiverSet;
    private SharedPreferences mPreferences;
    private EditText mUrlEditText;
    private Button mSearchButton;
    private ProgressBar mProgressBar;
    private TextView mWhatsHotTv;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getType()!=null &&
                intent.getType().equals("text/plain")) {
            setIntent(intent);
            if(!mPreferences.getBoolean(LOGGED_IN, false))
            {
                mPreferences.edit().putBoolean(LOGGED_IN, true)
                        .putInt(USER_ID, ID_GUEST).apply();
            }
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
                } catch (Exception e) {
                    mUrlEditText.setText("");
                    mUrlEditText.setHint(e.getMessage());
                    mProgressBar.setVisibility(GONE);
                    mSearchButton.setVisibility(View.VISIBLE);
                    mWhatsHotTv.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, R.string.error_retrieving_vid, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        mWhatsHotTv = findViewById(R.id.whats_hot_tv);
        mWhatsHotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WhatsHotActivity.class);
                startActivityForResult(intent, REQUEST_CODE_HOT_VIDEO);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if(mPreferences.getInt(USER_ID, ID_GUEST)!=ID_GUEST) checkToken();
        else harassGuest();
    }

    /**
     * Launches an AlertDialog to remind a guest user
     * of the advantages of registering.
     */
    private void harassGuest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.register_title);
        builder.setMessage(R.string.register_info);
        builder.setPositiveButton(R.string.yes_excited, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                takeGuestToSignUp();
            }
        });
        builder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkToken() {
        String token = "Bearer " + mPreferences.getString(ACCESS_TOKEN, "");
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        Call<ResponseBody> call = client.checkToken(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code()==200) {
                    return; //token is good, no need to refresh
                }
                else  {
                    refreshAccessToken();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, jObjError.getString("msg"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        if(mPreferences.getInt(USER_ID, ID_GUEST)==ID_GUEST) {
            menu.findItem(R.id.logout).setVisible(false);
            menu.findItem(R.id.account_settings).setVisible(false);
            menu.findItem(R.id.sign_up).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.logout: {
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
                return true;
            }
            case R.id.sign_up: {
                takeGuestToSignUp();
                return true;
            }
            case R.id.account_settings: {
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void takeGuestToSignUp() {
        mPreferences.edit().putBoolean(LOGGED_IN, false).putInt(USER_ID, ID_NONE).apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                        Toast.makeText(MainActivity.this, "Error code "
                                + response.code() + ": " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, R.string.unknown_error, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.security_error, Toast.LENGTH_LONG).show();
            }
        });
        callRefresh.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()!=200) {
                    try {
                        Toast.makeText(MainActivity.this, "Error code "
                                + response.code() + ": " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, R.string.security_error, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * @param url pass the full url. The method will parse the URL.
     */
    private void askForVideo (String url) throws Exception {

        if(!mIsReceiverSet)
        {
            //register the ResponseReceiver
            mReceiver = new ResponseReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(FETCH_VIDEO_INFO);
            registerReceiver(mReceiver, intentFilter);
        }

        mSearchButton.setVisibility(View.INVISIBLE);
        mWhatsHotTv.setVisibility(GONE);
        mProgressBar.setVisibility(View.VISIBLE);
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
            mWhatsHotTv.setVisibility(View.VISIBLE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK)
            return;
        if(requestCode==REQUEST_CODE_HOT_VIDEO)
        {
            if(data==null) return;
            try {
                String vidUrl = data.getStringExtra(WhatsHotActivity.EXTRA_VIDEO_URL);
                vidUrl = "https://www.youtube.com/watch?v=" + vidUrl;
                mUrlEditText.setText(vidUrl);
                askForVideo(vidUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        if(mReceiver!=null)
            unregisterReceiver(mReceiver);
        mIsReceiverSet = false;
        super.onPause();
    }
}
