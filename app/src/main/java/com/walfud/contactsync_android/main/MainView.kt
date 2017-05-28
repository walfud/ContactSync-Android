package com.walfud.contactsync_android.main

/**
 * Created by walfud on 2017/4/20.
 */

interface MainView {
    fun show(dataList: List<ViewContactData>)

    fun loading(show: Boolean)

    fun error(err: String)

    class ViewContactData {

        var name: String? = null
        var phoneList: List<String>? = null
        var status: Int = 0

        companion object {
            val STATUS_DEFAULT = 0
            val STATUS_LOCAL_ONLY = 1
            val STATUS_REMOTE_ONLY = 2
            val STATUS_CHANGED = 3
        }
    }
}
