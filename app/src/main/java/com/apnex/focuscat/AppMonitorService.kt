package com.apnex.focuscat

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppMonitorService : AccessibilityService() {

    private var lastPackage = ""
    private lateinit var timerManager: TimerManager

    override fun onCreate() {
        super.onCreate()
        timerManager = TimerManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Only block if timer is active
            if (timerManager.isTimerActive()) {
                // Check if it's a blocked app - always block, no duplicate prevention
                if (OverlayService.blockedApps.contains(packageName)) {
                    // Start overlay service - it will show the overlay automatically
                    val intent = Intent(this, OverlayService::class.java)
                    intent.putExtra("show_overlay", true)
                    startService(intent)
                }
            }
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Service connected
    }
}