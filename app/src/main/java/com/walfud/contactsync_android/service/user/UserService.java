package com.walfud.contactsync_android.service.user;

import android.content.Context;
import android.text.TextUtils;

import com.walfud.contactsync_android.service.prefs.PrefsModel;
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
        mPrefs.setUserPointer(id);
    }

    public String getId() {
        return getUserPrefs().id;
    }
    public String getToken() {
        return getUserPrefs().token;
    }
    public void setToken(String token) {
        PrefsModel.UserPrefsData userPrefsData = getUserPrefs();
        userPrefsData.token = token;
        mPrefs.setUserPrefs(userPrefsData.id, userPrefsData);
    }

    //
    private PrefsModel.UserPrefsData getUserPrefs() {
//        return ObjectUtils.getOpt(mPrefs.getUserPrefs(mPrefs.getUserPointer()), new PrefsModel.UserPrefsData());
        // DEBUG
        PrefsModel.UserPrefsData userPrefsData = new PrefsModel.UserPrefsData();
        userPrefsData.id = "57d50524-8e96-494d-8305-4bee3bccf62b";
        userPrefsData.token = "oid1";
        return userPrefsData;
    }
}
