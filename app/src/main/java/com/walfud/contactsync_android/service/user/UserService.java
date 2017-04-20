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
        return !TextUtils.isEmpty(getId());
    }

    public String getId() {
        return mPrefs.getUserPrefs(mPrefs.getUserPointer()).userId;
    }
}
