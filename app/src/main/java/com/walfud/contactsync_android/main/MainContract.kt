package com.walfud.contactsync_android.main;

/**
 * Created by walfud on 2017/6/27.
 */

class MainContract {
    interface MainView {
        fun show(dataList: List<ViewContactData>)

        fun loading(show: Boolean)

        fun error(err: String)

        data class ViewContactData(var name: String,
                                   var phoneList: List<String>,
                                   var status: Int = STATUS_DEFAULT) {
            companion object {
                val STATUS_DEFAULT = 0
                val STATUS_LOCAL_ONLY = 1
                val STATUS_REMOTE_ONLY = 2
                val STATUS_CHANGED = 3
            }
        }
    }

    interface MainPresenter {
        fun onLogin(oid: String, accessToken: String, refreshToken: String)
        fun onRefresh()
        fun onSync()
        fun onDownload()
        fun onUpload()
    }

}
