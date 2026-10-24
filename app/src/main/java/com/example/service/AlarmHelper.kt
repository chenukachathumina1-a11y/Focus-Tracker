package com.example.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.service.receiver.AlarmReceiver
import java.util.Calendar

object AlarmHelper {

    const val CHANNEL_ALARM_ID = "morning_alarm_channel"
    const val CHANNEL_FOCUS_ID = "focus_finished_channel"

    private var previewPlayer: MediaPlayer? = null
    private var previewToneGen: ToneGenerator? = null

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmChannel = NotificationChannel(
                CHANNEL_ALARM_ID,
                "Morning Study Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Morning wake-up alarm for study focus"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            val focusChannel = NotificationChannel(
                CHANNEL_FOCUS_ID,
                "Focus Session Completed",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when scheduled study or focus timer completes"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(alarmChannel)
            manager.createNotificationChannel(focusChannel)
        }
    }

    fun setMorningAlarm(context: Context, hour: Int, minute: Int, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_MORNING_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun triggerFocusFinishedNotification(context: Context, subjectName: String, durationMinutes: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_FOCUS_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Focus Session Completed!")
            .setContentText("Well done! You completed $durationMinutes mins on $subjectName.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2002, notification)
    }

    fun playPreviewTone(context: Context, toneName: String) {
        stopPreview()
        try {
            when (toneName) {
                "Gentle Sunrise Bells" -> {
                    previewToneGen = ToneGenerator(AudioManager.STREAM_ALARM, 85)
                    previewToneGen?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1200)
                }
                "Calm Temple Chime" -> {
                    previewToneGen = ToneGenerator(AudioManager.STREAM_ALARM, 75)
                    previewToneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 1500)
                }
                "Morning Forest Birds" -> {
                    previewToneGen = ToneGenerator(AudioManager.STREAM_ALARM, 80)
                    previewToneGen?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 1000)
                }
                "Energetic Digital Pulse" -> {
                    previewToneGen = ToneGenerator(AudioManager.STREAM_ALARM, 90)
                    previewToneGen?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 1500)
                }
                else -> {
                    val alertUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    previewPlayer = MediaPlayer().apply {
                        setDataSource(context, alertUri)
                        setAudioStreamType(AudioManager.STREAM_ALARM)
                        isLooping = false
                        prepare()
                        start()
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback beep
            try {
                previewToneGen = ToneGenerator(AudioManager.STREAM_ALARM, 80)
                previewToneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 1000)
            } catch (_: Exception) {}
        }
    }

    fun stopPreview() {
        try {
            previewPlayer?.stop()
            previewPlayer?.release()
            previewPlayer = null
            previewToneGen?.stopTone()
            previewToneGen?.release()
            previewToneGen = null
        } catch (_: Exception) {}
    }
}
