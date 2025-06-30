package com.lollipop.applist.desktop

import java.util.prefs.Preferences

object PreferencesHelper {

    private val prefs: Preferences by lazy {
        Preferences.userRoot().node(this.javaClass.packageName)
    }

    operator fun get(key: String): String? {
        return prefs.get(key, null)
    }

    operator fun set(key: String, value: String) {
        prefs.put(key, value)
    }

    operator fun set(key: String, value: Int) {
        prefs.putInt(key, value)
    }

    operator fun set(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
    }

    operator fun set(key: String, value: Long) {
        prefs.putLong(key, value)
    }

    operator fun set(key: String, value: Float) {
        prefs.putFloat(key, value)
    }

    operator fun set(key: String, value: Double) {
        prefs.putDouble(key, value)
    }

    fun opt(key: String, def: String): String {
        return prefs.get(key, def) ?: def
    }

    fun opt(key: String, def: Int): Int {
        return prefs.getInt(key, def)
    }

    fun opt(key: String, def: Boolean): Boolean {
        return prefs.getBoolean(key, def)
    }

    fun opt(key: String, def: Long): Long {
        return prefs.getLong(key, def)
    }

    fun opt(key: String, def: Float): Float {
        return prefs.getFloat(key, def)
    }

    fun opt(key: String, def: Double): Double {
        return prefs.getDouble(key, def)
    }

    fun remove(key: String) {
        prefs.remove(key)
    }

}