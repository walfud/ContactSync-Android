package com.walfud.contactsync_android;

import android.app.Application;
import android.content.Context;

import com.walfud.contactsync_android.service.network.NetworkService;
import com.walfud.contactsync_android.service.user.UserService;

/**
 * Created by walfud on 2017/4/20.
 */

public class ContactSyncApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        sAppContext = getApplicationContext();
        userService = new UserService(this);
        networkService = new NetworkService(this);
    }

    private static Context sAppContext;
    public static Context getAppContext() {
        return sAppContext;
    }



    // DEBUG
    public static UserService userService;
    public static NetworkService networkService;
}
