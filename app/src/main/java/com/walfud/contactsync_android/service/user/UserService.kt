package com.walfud.contactsync_android.service.user

import android.text.TextUtils
import com.walfud.contactsync_android.ContactSyncApplication
import com.walfud.contactsync_android.service.prefs.PrefsService

/**
 * Created by walfud on 2017/4/20.
 */

object UserService {
    private val mPrefs: PrefsService = PrefsService(ContactSyncApplication.appContext!!)

    val isLogin: Boolean
        get() = !TextUtils.isEmpty(token)

    fun changeUser(id: String) {
        mPrefs.oid = id
    }

    var token: String
        get() = mPrefs.userToken
        set(token) {
            mPrefs.userToken = token
        }
}
