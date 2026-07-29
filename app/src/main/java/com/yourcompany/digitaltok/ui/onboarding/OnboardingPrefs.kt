package com.yourcompany.digitaltok.ui.onboarding

import android.content.Context

object OnboardingPrefs {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_DONE = "onboarding_done"

    fun isDone(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DONE, false)
    }

    fun setDone(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DONE, true)
            .apply()
    }
}
