package com.walfud.contactsync_android.service.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by walfud on 2017/4/28.
 */

public class UserBean {
    public static class Request {
        public String username;
        public String password;
    }
    public static class Response {
        public int version;
        public ServerUserBean user;

        public static class ServerUserBean {
            public String username;
            public String id;
            @SerializedName("rgs_time") public long rgsTime;
            @SerializedName("acess_time") public long accessTime;
        }
    }
}
