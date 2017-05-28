package com.walfud.contactsync_android.service.network

import com.google.gson.annotations.SerializedName

/**
 * Created by walfud on 2017/4/28.
 */

class UserBean {
    class Request {
        var username: String? = null
        var password: String? = null
    }

    class Response {
        var version: Int = 0
        var user: ServerUserBean? = null

        class ServerUserBean {
            var username: String? = null
            var id: String? = null
            @SerializedName("rgs_time") var rgsTime: Long = 0
            @SerializedName("acess_time") var accessTime: Long = 0
        }
    }
}
