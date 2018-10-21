package com.youcmt.youdmcapp;

/**
 * Created by Stanislav Ostrovskii on 9/19/2018.
 * Copyright 2018 youcmt.com team. All rights reserved.
 *
 * The hosting activity of LoginFragment and RegisterFragment should implement
 * this interface in order to handle the respective callbacks upon a successful
 * login or registration.
 */

public interface LoginCallbacks {
    void onSuccessfulLogin(int userId);
    void onSuccessfulRegistration(int userId);
}
