package com.walfud.contactsync_android

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by walfud on 2017/4/20.
 */

val appContext = lazy { ContactSyncApplication.appContext }

class ContactSyncApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

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
        lateinit var appContext: Context
    }
}
