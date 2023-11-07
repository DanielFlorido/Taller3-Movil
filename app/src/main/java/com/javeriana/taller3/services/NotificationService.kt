package com.javeriana.taller3.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.javeriana.taller3.MapActivity
import com.javeriana.taller3.NotificationMapActivity
import com.javeriana.taller3.R
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.model.Usuario

class NotificationService : Service() {

    var notid = 0

    override fun onCreate(){
        super.onCreate()
        Log.i("NATA", "se esta escuchando notificacion")
        createNotificationChannel()
        databaseRealtimeService.notificationDispoible {
            var notification = buildNotification("${it.nombre} disponible", "Pulse para seguir a ${it.nombre}", R.drawable.baseline_circle_notifications_24, NotificationMapActivity::class.java, it)
            notify(notification)
            Log.i("NATA", "se envio la notificacion")
        }
    }
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel"
            val description = "channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test", name, importance)
            channel.setDescription(description)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel (channel)
        }
    }

    fun buildNotification(title: String, message: String, icon: Int, target: Class<*>, user: Usuario) : Notification {
        val builder =  NotificationCompat.Builder(this, "Test")
        builder.setSmallIcon(icon)
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        val intent = Intent(this, target)
        intent.putExtra("Latitud", user.key)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true) //Remueve la notificación cuando se toque
        return builder.build()
    }

    fun notify(notification: Notification) {
        notid++

        val notificationManager = NotificationManagerCompat.from(this)

        if(checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notid, notification)
        }
        if(notificationManager.areNotificationsEnabled()){
            notificationManager.notify(notid, notification)
        }else{
            Log.i("Daniel", "no tenemos permiso!")
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}