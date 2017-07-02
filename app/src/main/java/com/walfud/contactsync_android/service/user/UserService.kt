package com.walfud.contactsync_android.service.user

import android.text.TextUtils
import com.walfud.contactsync_android.service.prefs.PrefsService

/**
 * Created by walfud on 2017/4/20.
 */

object UserService {
    val isLogin: Boolean
        get() = !TextUtils.isEmpty(PrefsService.userTokenInternal)

    fun changeUser(id: String) {
        PrefsService.oid = id
    }

    var token: String
        get() = PrefsService.userToken
        set(token) {
            PrefsService.userToken = token
        }
}
