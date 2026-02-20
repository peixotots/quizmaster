package com.example.quizandroid.data.model

import android.content.Context
import android.content.SharedPreferences

class UserPrefsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_UID = "user_uid"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
    }

    fun saveUser(uid: String, email: String, name: String) {
        prefs.edit()
            .putString(KEY_UID, uid)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_NAME, "Jogador")
    }

    fun getUid(): String? = prefs.getString(KEY_UID, null)

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getName(): String? = prefs.getString(KEY_NAME, null)

    fun clearUser() {
        prefs.edit().clear().apply()
    }

    fun isUserLoggedIn(): Boolean = prefs.contains(KEY_UID)
}