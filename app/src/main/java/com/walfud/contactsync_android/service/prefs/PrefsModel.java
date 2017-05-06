package com.walfud.contactsync_android.service.prefs;

import java.util.Map;

/**
 * Created by walfud on 2017/4/20.
 */

/**
 * Shared Preference Data
 */
public class PrefsModel {

    public static final String PREFS_VERSION = "PREFS_VERSION";
    public int prefsVersion;

    public static final String PREFS_USER_POINTER = "PREFS_USER_POINTER";
    public String userPointer;              // User preference pointer

    public Map<String, UserPrefsData> map;  // user pointer - prefs data

    /**
     * User Specified Preference Data (Gson)
     */
    public static class UserPrefsData {
        public String id;
        public String token;
    }
}
