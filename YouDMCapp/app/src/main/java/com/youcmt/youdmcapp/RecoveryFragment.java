package com.youcmt.youdmcapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youcmt.youdmcapp.model.Email;
import com.youcmt.youdmcapp.model.ResetPasswordRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Stanislav Ostrovskii on 11/13/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class RecoveryFragment extends Fragment {
    private static final String TAG = "RecoveryFragment";
    private Button mSendButton;
    private Button mResetButton;
    private EditText mEmailEditText;
    private EditText mCodeEditText;
    private EditText mPassOneEditText;
    private EditText mPassTwoEditText;
    private Resources mResources;
    private String mEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mResources = getActivity().getResources();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recover, container, false);
        mSendButton = view.findViewById(R.id.send_reset_button);
        mResetButton = view.findViewById(R.id.reset_button);
        mEmailEditText = view.findViewById(R.id.email_recovery_et);
        mCodeEditText = view.findViewById(R.id.reset_code_et);
        mPassOneEditText = view.findViewById(R.id.recovery_et);
        mPassTwoEditText = view.findViewById(R.id.recovery_reenter_et);
        setListeners();
        view.findViewById(R.id.secondary_layout).setVisibility(View.INVISIBLE);
        return view;
    }

    private void setListeners() {
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEditText.getText().toString().trim();
                if(!ValidationUtils.isEmailAddressValid(email)) {
                    Toast.makeText(getActivity(), mResources.getString(R.string.invalid_email_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                mEmail = email;
                sendResetCode();
            }
        });
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resetCode = mCodeEditText.getText().toString().trim();
                String pass1 = mPassOneEditText.getText().toString().trim();
                String pass2 = mPassTwoEditText.getText().toString().trim();
                if(resetCode.length()!=7)
                {
                    Toast.makeText(getActivity(),
                            mResources.getString(R.string.invalid_code_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!ValidationUtils.doPasswordsMatch(pass1, pass2))
                {
                    Toast.makeText(getActivity(),
                            R.string.mismatched_passwords_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!ValidationUtils.isPasswordValid(pass1))
                {
                    Toast.makeText(getActivity(),
                            mResources.getString(R.string.weak_password_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ResetPasswordRequest request = new ResetPasswordRequest();
                if(mEmail!=null) request.setEmail(mEmail);
                request.setReset_code(resetCode);
                request.setNew_password(pass1);
                attemptPasswordReset(request);
            }
        });
    }

    private void sendResetCode() {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        Email email = new Email(mEmail);
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        Call<ResponseBody> response = client.confirmEmail(email, header);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if(code==200)
                {
                    Toast.makeText(getActivity(), mResources.getString(R.string.reset_code_sent_message), Toast.LENGTH_SHORT).show();
                    mSendButton.setText(mResources.getString(R.string.send_again));
                    getView().findViewById(R.id.secondary_layout).setVisibility(View.VISIBLE);
                }
                else
                {
                    try {
                        //try to retrieve the error message and place it in a toast
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
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                displayUnknownError();
            }
        });
    }

    private void attemptPasswordReset(ResetPasswordRequest request) {
        ApiEndPoint client = RetrofitClient.getApiEndpoint();
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        Call<ResponseBody> response = client.resetPassword(request, header);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200)
                {
                    Toast.makeText(getActivity(), R.string.password_reset_toast, Toast.LENGTH_SHORT).show();
                    FragmentTransaction fragmentTransaction =
                            getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());
                    fragmentTransaction.commit();
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
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                displayUnknownError();
            }
        });
    }

    private void displayUnknownError() {
        Toast.makeText(getActivity(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }
}
