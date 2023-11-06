package com.javeriana.taller3.autostart

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.javeriana.taller3.services.NotificationService

class AutoStart : BroadcastReceiver() {
    override fun onReceive(context: Context?, arg: Intent?) {
        if (arg != null) {
            if(arg.action == Intent.ACTION_BOOT_COMPLETED) {
                val intent = Intent(context, NotificationService::class.java);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context?.startForegroundService(intent);
                } else {
                    context?.startService(intent);
                }
                Log.i("Autostart", "started");
            }
        }
    }
}