package com.walfud.contactsync_android.service.prefs;

import android.content.SharedPreferences;

import com.f2prateek.rx.preferences2.Preference;
import com.google.gson.Gson;

/**
 * Created by walfud on 2017/4/20.
 */

class GsonPreferenceAdapter<T> implements Preference.Adapter<T> {
    private final Gson gson;
    private Class<T> clazz;

    public GsonPreferenceAdapter(Class<T> clazz) {
        this.gson = new Gson();
        this.clazz = clazz;
    }

    @Override
    public T get(String key, SharedPreferences preferences) {
        return gson.fromJson(preferences.getString(key, "{}"), clazz);
    }

    @Override
    public void set(String key, T value, SharedPreferences.Editor editor) {
        editor.putString(key, gson.toJson(value));
    }
}