package com.currenthealth.raptorhealthmonitor.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import com.currenthealth.raptorhealthmonitor.MainActivity

class BootCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        Log.i(TAG(), "onReceive: CH: BOOT COMPLETED")
        context?.startActivity(launchIntent)
    }
}