package com.walfud.contactsync_android.service.network

import com.apollographql.apollo.ApolloClient
import com.walfud.contactsync_android.SyncMutation
import com.walfud.contactsync_android.type.ContactInputType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by walfud on 2017/4/28.
 */

object NetworkService {

    private val mOkHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .connectTimeout((10 * 1000).toLong(), TimeUnit.MILLISECONDS)
            .readTimeout((30 * 1000).toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout((30 * 1000).toLong(), TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(false)
            .build()
    private val mNetworkInterface: NetworkInterface = Retrofit.Builder()
            .client(mOkHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://contactsync.walfud.com/")
            .build()
            .create(NetworkInterface::class.java)
    private val mApolloClient: ApolloClient = ApolloClient.builder()
            .serverUrl("http://contactsync.walfud.com/graphql")
            .okHttpClient(mOkHttpClient)
            .build()

    fun sync(token: String, contactList: List<ContactInputType>): SyncMutation.Data {
        return mApolloClient.newCall(SyncMutation.builder()
                .token(token)
                .contacts(contactList)
                .build())
                .execute()
                .data()
    }
}
