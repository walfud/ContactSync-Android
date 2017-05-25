package com.walfud.contactsync_android.service.network;

import android.content.Context;

import com.apollographql.android.rx2.Rx2Apollo;
import com.apollographql.apollo.ApolloClient;
import com.walfud.contactsync_android.SyncMutation;
import com.walfud.contactsync_android.type.ContactInputType;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by walfud on 2017/4/28.
 */

public class NetworkService {

    private OkHttpClient mOkHttpClient;
    private NetworkInterface mNetworkInterface;
    private ApolloClient mApolloClient;

    public NetworkService(Context context) {
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor())
                .connectTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://contactsync.walfud.com/")
                .build();
        mNetworkInterface = retrofit.create(NetworkInterface.class);

        mApolloClient = ApolloClient.builder()
                .serverUrl("http://contactsync.walfud.com/graphql")
                .okHttpClient(mOkHttpClient)
                .build();
    }

    public Single<SyncMutation.Data> sync(String token, List<ContactInputType> contactList) {
        return Rx2Apollo.from(mApolloClient.newCall(
                SyncMutation.builder()
                        .token(token)
                        .contacts(contactList)
                        .build()
        ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
