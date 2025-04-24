package com.example.focusbuddy

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.content.Intent

class BlockAppService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null || event.packageName == null) return

        if (event.packageName == "com.instagram.android") {

            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(homeIntent)
            }
        }
    }

    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.packageNames = arrayOf("com.instagram.android")
        this.serviceInfo = info
    }
}

