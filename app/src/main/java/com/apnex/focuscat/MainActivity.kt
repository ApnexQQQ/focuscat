package com.apnex.focuscat

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timerManager: TimerManager
    private lateinit var statusIndicator: View
    private lateinit var statusText: TextView
    private lateinit var checkOverlay: View
    private lateinit var checkAccessibility: View
    private lateinit var pulseEffect: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        timerManager = TimerManager(this)
        
        // Initialize views
        statusIndicator = findViewById(R.id.statusIndicator)
        statusText = findViewById(R.id.statusText)
        checkOverlay = findViewById(R.id.checkOverlay)
        checkAccessibility = findViewById(R.id.checkAccessibility)
        pulseEffect = findViewById(R.id.pulseEffect)
        
        // Start pulse animation
        startPulseAnimation()
        
        // Start breathing animation on cat
        startBreathingAnimation()
        
        // Update status
        updateStatus()
        
        // Setup click listeners
        findViewById<FrameLayout>(R.id.btnStart).setOnClickListener {
            startFocusMode()
        }

        findViewById<FrameLayout>(R.id.btnTimer).setOnClickListener {
            showTimerDialog()
        }
        
        findViewById<FrameLayout>(R.id.cardOverlay).setOnClickListener {
            requestOverlayPermission()
        }
        
        findViewById<FrameLayout>(R.id.cardAccessibility).setOnClickListener {
            requestAccessibilityPermission()
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateStatus()
    }
    
    private fun startPulseAnimation() {
        val animator = ObjectAnimator.ofFloat(pulseEffect, "alpha", 0.3f, 0f)
        animator.duration = 2000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()
    }
    
    private fun startBreathingAnimation() {
        val breathingOverlay = findViewById<View>(R.id.breathingOverlay)
        val animator = ObjectAnimator.ofFloat(breathingOverlay, "alpha", 0.1f, 0.3f)
        animator.duration = 3000
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()
    }
    
    private fun updateStatus() {
        val hasOverlay = hasOverlayPermission()
        val hasAccessibility = hasAccessibilityPermission()
        
        // Update checkmarks
        checkOverlay.setBackgroundResource(if (hasOverlay) R.drawable.ic_check_active else R.drawable.ic_check_inactive)
        checkAccessibility.setBackgroundResource(if (hasAccessibility) R.drawable.ic_check_active else R.drawable.ic_check_inactive)
        
        // Update status indicator
        when {
            timerManager.isTimerActive() -> {
                statusIndicator.setBackgroundResource(R.drawable.status_dot_active)
                statusText.text = "Protection Active"
                statusText.setTextColor(getColor(R.color.emerald_green))
            }
            hasOverlay && hasAccessibility -> {
                statusIndicator.setBackgroundResource(R.drawable.status_dot_ready)
                statusText.text = "Ready to activate"
                statusText.setTextColor(getColor(R.color.text_primary))
            }
            else -> {
                statusIndicator.setBackgroundResource(R.drawable.status_dot_inactive)
                statusText.text = "Setup required"
                statusText.setTextColor(getColor(R.color.text_muted))
            }
        }
    }

    private fun startFocusMode() {
        if (!hasOverlayPermission() || !hasAccessibilityPermission()) {
            Toast.makeText(this, "Please enable all permissions first", Toast.LENGTH_LONG).show()
            return
        }
        
        startService(Intent(this, OverlayService::class.java))
        
        // Show success animation
        val btnStart = findViewById<FrameLayout>(R.id.btnStart)
        btnStart.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                btnStart.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
        
        Toast.makeText(this, "FocusCat is watching over you 🐱", Toast.LENGTH_SHORT).show()
        updateStatus()
    }

    private fun showTimerDialog() {
        if (timerManager.isTimerActive()) {
            val (hours, minutes) = timerManager.getRemainingTime()
            val timeStr = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
            
            AlertDialog.Builder(this, R.style.PremiumDialog)
                .setTitle("Protection Active")
                .setMessage("$timeStr remaining\n\nFocusCat is protecting your peace of mind.")
                .setPositiveButton("Stop Protection") { _, _ ->
                    timerManager.cancelTimer()
                    updateStatus()
                    Toast.makeText(this, "Protection ended", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Continue", null)
                .show()
        } else {
            val options = arrayOf(
                "Quick Focus — 5 minutes",
                "Deep Work — 25 minutes", 
                "Study Session — 1 hour",
                "Evening Wind Down — 2 hours",
                "Bedtime Mode — 8 hours",
                "School Day — 7 hours"
            )
            val minutes = arrayOf(5, 25, 60, 120, 480, 420)

            AlertDialog.Builder(this, R.style.PremiumDialog)
                .setTitle("Set Protection Timer")
                .setItems(options) { _, which ->
                    timerManager.setTimer(minutes[which])
                    updateStatus()
                    
                    val message = when(which) {
                        0 -> "Quick focus session started"
                        1 -> "Deep work mode activated"
                        2 -> "Study session began"
                        3 -> "Evening wind down started"
                        4 -> "Sweet dreams mode on"
                        5 -> "School day protection active"
                        else -> "Timer set"
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun requestOverlayPermission() {
        if (!hasOverlayPermission()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } else {
            Toast.makeText(this, "Overlay permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(this, "Find FocusCat and enable it", Toast.LENGTH_LONG).show()
    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun hasAccessibilityPermission(): Boolean {
        // Check if accessibility service is enabled
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains("com.apnex.focuscat") == true
    }
}