package com.walfud.contactsync_android.service.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.walfud.walle.collection.CollectionUtils;
import com.walfud.walle.lang.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by walfud on 2017/4/20.
 */

public class PrefsService {

    private Context mContext;
    RxSharedPreferences mRxSharedPreferences;
    private PrefsModel mPrefsModel;

    public PrefsService(Context context) {
        this.mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRxSharedPreferences = RxSharedPreferences.create(sharedPreferences);

        //
        mPrefsModel = new PrefsModel();
        mPrefsModel.prefsVersion = mRxSharedPreferences.getInteger(PrefsModel.PREFS_VERSION).get();
        mPrefsModel.userPointer = mRxSharedPreferences.getString(PrefsModel.PREFS_USER_POINTER, "").get();
        mPrefsModel.map = new HashMap<>();
    }

    public int getPrefsVersion() {
        return mPrefsModel.prefsVersion;
    }
    public void setPrefsVersion(int newPrefsVersion) {
        mPrefsModel.prefsVersion = newPrefsVersion;
        mRxSharedPreferences.getInteger(PrefsModel.PREFS_VERSION, 1).set(newPrefsVersion);
    }

    public String getUserPointer() {
        return mPrefsModel.userPointer;
    }
    public void setUserPointer(String userPointer) {
        mPrefsModel.userPointer = userPointer;
        mRxSharedPreferences.getString(PrefsModel.PREFS_USER_POINTER, "").set(userPointer);
    }

    public PrefsModel.UserPrefsData getUserPrefs(String userId) {
        PrefsModel.UserPrefsData userPrefsData = CollectionUtils.find(mPrefsModel.map.values(), (Predicate<PrefsModel.UserPrefsData>) userPrefsData1 -> ObjectUtils.isEqual(userPrefsData1.userId, userId));
        if (userPrefsData == null) {
            userPrefsData = mRxSharedPreferences.getObject(PrefsModel.PREFS_USER_POINTER, new GsonPreferenceAdapter<>(PrefsModel.UserPrefsData.class)).get();
            mPrefsModel.map.put(userId, userPrefsData);
        }

        return userPrefsData;
    }
    public void setUserPrefs(String userId, PrefsModel.UserPrefsData newUserPrefsData) {
        for (Map.Entry<String, PrefsModel.UserPrefsData> pointer_userPrefsData : mPrefsModel.map.entrySet()) {
            if (ObjectUtils.isEqual(pointer_userPrefsData.getValue().userId, userId)) {
                pointer_userPrefsData.setValue(newUserPrefsData);
                mRxSharedPreferences.getObject(PrefsModel.PREFS_USER_POINTER, new GsonPreferenceAdapter<>(PrefsModel.UserPrefsData.class)).set(newUserPrefsData);
                break;
            }
        }
    }
}
