package com.uph.finalproject.application;

import android.app.Application;

import com.uph.finalproject.data.model.LoggedInUser;

import java.util.ArrayList;

public class MyApplication extends Application {

    private LoggedInUser loggedInUser;
    private String DATABASE_NAME = "ujicobadb";

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public String getDATABASE_NAME() {
        return DATABASE_NAME;
    }
}
