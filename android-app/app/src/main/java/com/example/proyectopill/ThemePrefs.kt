package com.example.proyectopill

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object ThemePrefs {
    private const val PREFS = "theme_prefs"
    private const val KEY = "mode"

    fun getDefaultMode() = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    fun saveMode(ctx: Context, mode: Int) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putInt(KEY, mode)
            apply()
        }
    }

    fun loadMode(ctx: Context): Int {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY, getDefaultMode())
    }
}
