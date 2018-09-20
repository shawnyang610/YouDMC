package com.youcmt.youdmcapp.model;

/**
 * Created by stan_ on 9/18/2018.
 *
 */

public class UserRequest {
    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
