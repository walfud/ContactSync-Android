package com.walfud.contactsync_android.main;

/**
 * Created by walfud on 2017/4/20.
 */

public interface MainPresenter {
    void onLogin(String oid, String accessToken, String refreshToken);
    void onRefresh();
    void onSync();
}
