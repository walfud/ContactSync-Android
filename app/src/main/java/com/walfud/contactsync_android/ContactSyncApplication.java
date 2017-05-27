package com.walfud.contactsync_android;

import android.app.Application;
import android.content.Context;

import com.walfud.contactsync_android.service.network.NetworkService;
import com.walfud.contactsync_android.service.user.UserService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

        initRealm();
    }

    //
    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("myrealm.realm")
//                .encryptionKey(getKey())
                .schemaVersion(1)
//                .modules(new MySchemaModule())
//                .migration(new MyMigration())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private static Context sAppContext;

    public static Context getAppContext() {
        return sAppContext;
    }

    public static UserService userService;
    public static NetworkService networkService;
}
