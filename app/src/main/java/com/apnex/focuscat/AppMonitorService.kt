package com.apnex.focuscat

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppMonitorService : AccessibilityService() {

    private var lastPackage = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Avoid duplicate triggers
            if (packageName == lastPackage) return
            lastPackage = packageName

            // Check if it's a blocked app
            if (OverlayService.blockedApps.contains(packageName)) {
                if (OverlayService.isRunning) {
                    // Start overlay service and show the cat
                    val intent = Intent(this, OverlayService::class.java)
                    startService(intent)
                    
                    // Show the overlay
                    (applicationContext.getSystemService(OVERLAY_SERVICE) as? android.view.WindowManager)?.let {
                        // Use a handler to show overlay after service starts
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            val service = OverlayService()
                            service.showOverlay()
                        }, 100)
                    }
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