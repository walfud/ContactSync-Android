package com.walfud.contactsync_android.service.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.walfud.walle.algorithm.Comparator;
import com.walfud.walle.collection.CollectionUtils;
import com.walfud.walle.lang.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by walfud on 2017/4/20.
 */

public class PrefsService {

    private Context mContext;
    RxSharedPreferences mRxSharedPreferences;
    private PrefsData mPrefsData;

    public PrefsService(Context context) {
        this.mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

        //
        mPrefsData = new PrefsData();
        mPrefsData.prefsVersion = mRxSharedPreferences.getInteger(PrefsData.PREFS_VERSION).get();
        mPrefsData.userPointer = mRxSharedPreferences.getString(PrefsData.PREFS_USER_POINTER, "").get();
        mPrefsData.map = new HashMap<>();
    }

    public int getPrefsVersion() {
        return mPrefsData.prefsVersion;
    }
    public void setPrefsVersion(int newPrefsVersion) {
        mPrefsData.prefsVersion = newPrefsVersion;
        mRxSharedPreferences.getInteger(PrefsData.PREFS_VERSION, 1).set(newPrefsVersion);
    }

    public String getUserPointer() {
        return mPrefsData.userPointer;
    }
    public void setUserPointer(String userPointer) {
        mPrefsData.userPointer = userPointer;
        mRxSharedPreferences.getString(PrefsData.PREFS_USER_POINTER, "").set(userPointer);
    }

    public PrefsData.UserPrefsData getUserPrefs(String userId) {
        PrefsData.UserPrefsData userPrefsData = CollectionUtils.find(mPrefsData.map.values(), (Comparator<Void, PrefsData.UserPrefsData>) (foo, userPrefsData1) -> ObjectUtils.isEqual(userPrefsData1.userId, userId) ? 0 : -1);
        if (userPrefsData == null) {
            userPrefsData = mRxSharedPreferences.getObject(PrefsData.PREFS_USER_POINTER, new GsonPreferenceAdapter<>(PrefsData.UserPrefsData.class)).get();
            mPrefsData.map.put(userId, userPrefsData);
        }

        return userPrefsData;
    }
    public void setUserPrefs(String userId, PrefsData.UserPrefsData newUserPrefsData) {
        for (Map.Entry<String, PrefsData.UserPrefsData> pointer_userPrefsData : mPrefsData.map.entrySet()) {
            if (ObjectUtils.isEqual(pointer_userPrefsData.getValue().userId, userId)) {
                pointer_userPrefsData.setValue(newUserPrefsData);
                mRxSharedPreferences.getObject(PrefsData.PREFS_USER_POINTER, new GsonPreferenceAdapter<>(PrefsData.UserPrefsData.class)).set(newUserPrefsData);
                break;
            }
        }
    }
}
