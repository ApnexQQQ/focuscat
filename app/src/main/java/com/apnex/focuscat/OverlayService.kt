package com.apnex.focuscat

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import android.net.Uri

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var countdownTimer: CountDownTimer? = null

    companion object {
        var isRunning = false
        var blockedApps = listOf(
            "com.instagram.android",
            "com.zhiliaoapp.musically", // TikTok
            "com.facebook.katana",
            "com.twitter.android",
            "com.snapchat.android",
            "com.reddit.frontpage"
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        removeOverlay()
        countdownTimer?.cancel()
    }

    fun showOverlay() {
        if (overlayView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_cat, null)

        val countdownText = overlayView?.findViewById<TextView>(R.id.countdownText)
        val btnGoHome = overlayView?.findViewById<Button>(R.id.btnGoHome)
        val catVideo = overlayView?.findViewById<VideoView>(R.id.catVideo)

        // Play the cat video
        catVideo?.let {
            val videoUri = Uri.parse("android.resource://$packageName/${R.raw.cat_video}")
            it.setVideoURI(videoUri)
            it.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                it.start()
            }
        }

        btnGoHome?.setOnClickListener {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(homeIntent)
            removeOverlay()
        }

        windowManager?.addView(overlayView, params)

        // Update countdown every second
        val timerManager = TimerManager(this)
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        
        val updateRunnable = object : Runnable {
            override fun run() {
                if (!timerManager.isTimerActive()) {
                    removeOverlay()
                    return
                }
                
                val (hours, minutes) = timerManager.getRemainingTime()
                val seconds = timerManager.getRemainingSeconds()
                val timeStr = if (hours > 0) {
                    String.format("%02d:%02d:%02d remaining", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d remaining", minutes, seconds)
                }
                countdownText?.text = timeStr
                
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateRunnable)
    }

    fun removeOverlay() {
        // Stop video playback
        overlayView?.findViewById<VideoView>(R.id.catVideo)?.stopPlayback()
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
        countdownTimer?.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Show overlay when requested
        if (intent?.getBooleanExtra("show_overlay", false) == true) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                showOverlay()
            }
        }
        return START_STICKY
    }
}