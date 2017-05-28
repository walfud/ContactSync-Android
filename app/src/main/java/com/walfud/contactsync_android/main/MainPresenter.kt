package com.walfud.contactsync_android.main

/**
 * Created by walfud on 2017/4/20.
 */

interface MainPresenter {
    fun onLogin(oid: String, accessToken: String, refreshToken: String)
    fun onRefresh()
    fun onSync()
}
