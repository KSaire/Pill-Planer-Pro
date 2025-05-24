package com.example.proyectopill

import android.content.Context
import android.content.SharedPreferences

object SessionPrefs {
    private const val PREFS_NAME = "pillplanner_prefs"
    private const val KEY_LAST_USER = "last_user_id"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLastUser(ctx: Context, userId: Int) {
        prefs(ctx).edit().putInt(KEY_LAST_USER, userId).apply()
    }

    fun getLastUser(ctx: Context): Int =
        prefs(ctx).getInt(KEY_LAST_USER, -1)
}