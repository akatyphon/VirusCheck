package com.madinaappstudio.viruscheck.utils

import android.content.Context

class SharedPreference(private val context: Context) {

    private val prefName = "USER_LOGIN_SESSION"

    fun saveUserSession(isLoggedIn: Boolean, userId: String, name: String) {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().apply {
            putBoolean("IS_LOGGED_IN", isLoggedIn)
            putString("USER_ID", userId)
            putString("NAME", name)
        }.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            .getBoolean("IS_LOGGED_IN", false)
    }

    fun getUserId(): String? {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            .getString("USER_ID", null)
    }

    fun getUserName(): String? {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            .getString("NAME", null)
    }

    fun clearUserSession() {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().apply {
            clear()
            apply()
        }
    }

}