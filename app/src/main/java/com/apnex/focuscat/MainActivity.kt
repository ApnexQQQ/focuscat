package com.apnex.focuscat

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnEnableOverlay).setOnClickListener {
            requestOverlayPermission()
        }

        findViewById<Button>(R.id.btnEnableAccessibility).setOnClickListener {
            requestAccessibilityPermission()
        }

        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            if (hasOverlayPermission() && hasAccessibilityPermission()) {
                startService(Intent(this, OverlayService::class.java))
                Toast.makeText(this, "FocusCat is watching! 🐱", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enable both permissions first!", Toast.LENGTH_LONG).show()
            }
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
            Toast.makeText(this, "Overlay permission already granted!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(this, "Find 'FocusCat' in Accessibility Services and enable it", Toast.LENGTH_LONG).show()
    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun hasAccessibilityPermission(): Boolean {
        // Accessibility service checks itself when started
        return true
    }
}