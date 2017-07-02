package com.walfud.contactsync_android.service.prefs

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.walfud.contactsync_android.appContext
import com.walfud.walle.lang.ObjectUtils

/**
 * Created by walfud on 2017/4/20.
 */

private val PREFS_VERSION = "PREFS_VERSION"
private val PREFS_OID = "PREFS_OID"

object PrefsService {
    internal var mRxSharedPreferences: RxSharedPreferences

    init {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        mRxSharedPreferences = RxSharedPreferences.create(sharedPreferences)
    }

    // Shared
    var prefsVersion: Int
        get() = mRxSharedPreferences.getInteger(PREFS_VERSION).get()!!
        set(newPrefsVersion) = mRxSharedPreferences.getInteger(PREFS_VERSION).set(newPrefsVersion)

    var oid: String
        get() = ObjectUtils.getOpt(mRxSharedPreferences.getString(PREFS_OID).get(), "00000000-0000-0000-0000-000000000000")
        set(oid) = mRxSharedPreferences.getString(PREFS_OID).set(oid)

    // User
    var userToken
        get() = userTokenInternal!!
        set(token) {
            userTokenInternal = token
        }
    var userTokenInternal: String?
        get() = getUserPrefs(oid).token
        set(token) {
            val oid = oid
            val userPrefs = getUserPrefs(oid)
            userPrefs.token = token
            setUserPrefs(oid, userPrefs)
        }

    //
    private fun getUserPrefs(oid: String): UserPrefs {
        var userPrefs = mRxSharedPreferences.getObject(oid, GsonPreferenceAdapter(UserPrefs::class.java)).get()
        if (userPrefs == null) {
            userPrefs = UserPrefs()
            setUserPrefs(oid, userPrefs)
        }
        return userPrefs
    }

    private fun setUserPrefs(oid: String, userPrefs: UserPrefs) {
        mRxSharedPreferences.getObject(oid, GsonPreferenceAdapter(UserPrefs::class.java)).set(userPrefs)
    }

    class UserPrefs {
        var token: String? = null
    }
}