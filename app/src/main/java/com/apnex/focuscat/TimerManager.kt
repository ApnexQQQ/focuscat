package com.apnex.focuscat

import android.content.Context
import android.content.SharedPreferences

class TimerManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("focuscat_timer", Context.MODE_PRIVATE)

    fun setTimer(minutes: Int) {
        val endTime = System.currentTimeMillis() + (minutes * 60 * 1000)
        prefs.edit()
            .putLong("timer_end", endTime)
            .putBoolean("timer_active", true)
            .putInt("timer_duration", minutes)
            .apply()
    }

    fun isTimerActive(): Boolean {
        if (!prefs.getBoolean("timer_active", false)) return false
        val endTime = prefs.getLong("timer_end", 0)
        val active = System.currentTimeMillis() < endTime
        if (!active) {
            // Timer expired, clear it
            prefs.edit().putBoolean("timer_active", false).apply()
        }
        return active
    }

    fun getRemainingTime(): Pair<Int, Int> {
        val endTime = prefs.getLong("timer_end", 0)
        val remainingMs = endTime - System.currentTimeMillis()
        if (remainingMs <= 0) return Pair(0, 0)
        
        val totalMinutes = (remainingMs / (60 * 1000)).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return Pair(hours, minutes)
    }

    fun getRemainingSeconds(): Int {
        val endTime = prefs.getLong("timer_end", 0)
        val remainingMs = endTime - System.currentTimeMillis()
        if (remainingMs <= 0) return 0
        return ((remainingMs % (60 * 1000)) / 1000).toInt()
    }

    fun cancelTimer() {
        prefs.edit()
            .putBoolean("timer_active", false)
            .remove("timer_end")
            .remove("timer_duration")
            .apply()
    }
}