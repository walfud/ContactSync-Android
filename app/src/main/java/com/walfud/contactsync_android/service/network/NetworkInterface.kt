package com.walfud.contactsync_android.service.network

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by walfud on 2017/4/28.
 */

interface NetworkInterface {
    @POST("/user")
    fun regist(requestUserBean: UserBean.Request): Observable<Response<UserBean.Response>>

    @GET("/user")
    fun login(requestUserBean: UserBean.Request): Observable<Response<UserBean.Response>>
}
