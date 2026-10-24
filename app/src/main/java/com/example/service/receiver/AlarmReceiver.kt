package com.example.service.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.service.AlarmHelper

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MORNING_ALARM = "com.aistudio.focustracker.ACTION_MORNING_ALARM"
        const val ACTION_FOCUS_FINISHED = "com.aistudio.focustracker.ACTION_FOCUS_FINISHED"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        when (action) {
            ACTION_MORNING_ALARM -> {
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    1002,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, AlarmHelper.CHANNEL_ALARM_ID)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle("🌅 Morning Study Wake-Up!")
                    .setContentText("Rise and shine! Time for your morning focus session.")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(1003, notification)
            }
            ACTION_FOCUS_FINISHED -> {
                val subject = intent.getStringExtra("subject_name") ?: "Study Session"
                val minutes = intent.getLongExtra("duration_minutes", 25L)
                AlarmHelper.triggerFocusFinishedNotification(context, subject, minutes)
            }
        }
    }
}
