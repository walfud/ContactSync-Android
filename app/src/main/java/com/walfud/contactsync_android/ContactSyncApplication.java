package com.walfud.contactsync_android;

import android.app.Application;
import android.content.Context;

/**
 * Created by walfud on 2017/4/20.
 */

public class ContactSyncApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        sAppContext = getApplicationContext();
    }

    private static Context sAppContext;
    public static Context getAppContext() {
        return sAppContext;
    }
}
