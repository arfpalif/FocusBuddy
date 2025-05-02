package com.example.focusbuddy.data

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.focusbuddy.ui.BlockedActivity

class AppBlockerAccessibilityService: AccessibilityService() {

    private val blockedPackages = listOf(
        "com.instagram.android",
        "com.facebook.katana",
        "com.twitter.android",
        "com.zhiliaoapp.musically",
        "com.ss.android.ugc.trill", // TikTok global
        "com.tiktok.android"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        val prefs = getSharedPreferences("usage_data", Context.MODE_PRIVATE)
        val totalMinutes = prefs.getInt("sosmed_minutes", 0)

        if (packageName in blockedPackages && totalMinutes > 240) {
            val blockIntent = Intent(this, BlockedActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(blockIntent)
        }
    }

    override fun onInterrupt() {
        // do nothing
    }
}