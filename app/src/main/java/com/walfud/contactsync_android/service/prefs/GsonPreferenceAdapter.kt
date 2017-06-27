package com.walfud.contactsync_android.service.prefs

import android.content.SharedPreferences

import com.f2prateek.rx.preferences2.Preference
import com.google.gson.Gson

/**
 * Created by walfud on 2017/4/20.
 */

class GsonPreferenceAdapter<T>(private val clazz: Class<T>) : Preference.Adapter<T> {
    private val gson: Gson = Gson()

    override fun get(key: String, preferences: SharedPreferences): T {
        return gson.fromJson(preferences.getString(key, "{}"), clazz)
    }

    override fun set(key: String, value: T, editor: SharedPreferences.Editor) {
        editor.putString(key, gson.toJson(value))
    }
}