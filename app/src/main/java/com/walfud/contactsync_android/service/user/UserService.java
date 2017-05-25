package com.walfud.contactsync_android.service.user;

import android.content.Context;
import android.text.TextUtils;

import com.walfud.contactsync_android.service.prefs.PrefsService;

/**
 * Created by walfud on 2017/4/20.
 */

public class UserService {

    private Context mContext;
    private PrefsService mPrefs;

    public UserService(Context context) {
        this.mContext = context;
        mPrefs = new PrefsService(context);
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(getToken());
    }

    public void changeUser(String id) {
        mPrefs.setOid(id);
    }

    public String getToken() {
        return mPrefs.getUserToken();
    }

    public void setToken(String token) {
        mPrefs.setUserToken(token);
    }
}
