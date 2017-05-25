package com.walfud.contactsync_android.service.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.walfud.walle.lang.ObjectUtils;

/**
 * Created by walfud on 2017/4/20.
 */

public class PrefsService {

    private static final String PREFS_VERSION = "PREFS_VERSION";
    private static final String PREFS_OID = "PREFS_OID";

    private Context mContext;
    RxSharedPreferences mRxSharedPreferences;

    public PrefsService(Context context) {
        this.mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRxSharedPreferences = RxSharedPreferences.create(sharedPreferences);
    }

    // Shared
    public int getPrefsVersion() {
        return mRxSharedPreferences.getInteger(PREFS_VERSION).get();
    }
    public void setPrefsVersion(int newPrefsVersion) {
        mRxSharedPreferences.getInteger(PREFS_VERSION).set(newPrefsVersion);
    }

    public String getOid() {
        return ObjectUtils.getOpt(mRxSharedPreferences.getString(PREFS_OID).get(), "00000000-0000-0000-0000-000000000000");
    }
    public void setOid(String oid) {
        mRxSharedPreferences.getString(PREFS_OID).set(oid);
    }

    // User
    public String getUserToken() {
        return getUserPrefs(getOid()).token;
    }
    public void setUserToken(String token) {
        String oid = getOid();
        UserPrefs userPrefs = getUserPrefs(oid);
        userPrefs.token = token;
        setUserPrefs(oid, userPrefs);
    }

    //
    private UserPrefs getUserPrefs(String oid) {
        UserPrefs userPrefs = mRxSharedPreferences.getObject(oid, new GsonPreferenceAdapter<>(UserPrefs.class)).get();
        if (userPrefs == null) {
            userPrefs = new UserPrefs();
            setUserPrefs(oid, userPrefs);
        }
        return userPrefs;
    }
    private void setUserPrefs(String oid, UserPrefs userPrefs) {
        mRxSharedPreferences.getObject(oid, new GsonPreferenceAdapter<>(UserPrefs.class)).set(userPrefs);
    }

    public static class UserPrefs {
        public String token;
    }
}