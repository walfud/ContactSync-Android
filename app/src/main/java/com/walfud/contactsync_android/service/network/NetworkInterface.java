package com.walfud.contactsync_android.service.network;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by walfud on 2017/4/28.
 */

public interface NetworkInterface {
    @POST("/user")
    Observable<Response<UserBean.Response>> regist(UserBean.Request requestUserBean);

    @GET("/user")
    Observable<Response<UserBean.Response>> login(UserBean.Request requestUserBean);
}
