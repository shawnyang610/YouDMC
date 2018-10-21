package com.youcmt.youdmcapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static com.youcmt.youdmcapp.Constants.ID_GUEST;

/**
 * Created by Stanislav Ostrovskii on 9/18/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 */

public class SelectionFragment extends Fragment {
    private SharedPreferences mPreferences;
    private Button mLoginButton;
    private Button mRegisterButton;
    private Button mSkipButton;
    private LoginCallbacks mHostingActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        mHostingActivity =(LoginCallbacks) getActivity();
        View view = inflater.inflate(R.layout.fragment_selection, container, false);
        mLoginButton = view.findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());
                fragmentTransaction.commit();
            }
        });
        mRegisterButton = view.findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_container, new RegisterFragment());
                fragmentTransaction.commit();
            }
        });
        mSkipButton = view.findViewById(R.id.skip_button);
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHostingActivity.onSuccessfulLogin(ID_GUEST);
            }
        });
        return view;
    }

}
