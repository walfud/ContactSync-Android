package com.walfud.contactsync_android

import android.app.Application
import android.content.Context

import com.walfud.contactsync_android.service.network.NetworkService
import com.walfud.contactsync_android.service.user.UserService

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by walfud on 2017/4/20.
 */

class ContactSyncApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        userService = UserService
        networkService = NetworkService(this)

        initRealm()
    }

    //
    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                //                .name("myrealm.realm")
                //                .encryptionKey(getKey())
                .schemaVersion(1)
                //                .modules(new MySchemaModule())
                //                .migration(new MyMigration())
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }

    companion object {
        var appContext: Context? = null
        var userService: UserService? = null
        var networkService: NetworkService? = null
    }
}
