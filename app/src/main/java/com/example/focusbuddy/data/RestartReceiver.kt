package com.example.focusbuddy.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RestartReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val restartIntent = Intent(context, UsageStatsService::class.java)
        context?.startForegroundService(restartIntent)
    }
}