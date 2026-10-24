package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FocusTimerService : Service() {

    companion object {
        const val CHANNEL_ID = "focus_timer_ongoing_channel"
        const val NOTIFICATION_ID = 3001

        const val EXTRA_SUBJECT_NAME = "extra_subject_name"
        const val EXTRA_END_TIME_MS = "extra_end_time_ms"
        const val EXTRA_TOTAL_SECONDS = "extra_total_seconds"

        fun startService(context: Context, subjectName: String, endTimeMs: Long, totalSeconds: Long) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                putExtra(EXTRA_SUBJECT_NAME, subjectName)
                putExtra(EXTRA_END_TIME_MS, endTimeMs)
                putExtra(EXTRA_TOTAL_SECONDS, totalSeconds)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FocusTimerService::class.java)
            context.stopService(intent)
        }
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var monitorJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "FocusTracker::TimerWakeLock"
        ).apply {
            setReferenceCounted(false)
            acquire(12 * 60 * 60 * 1000L) // Safety 12-hour timeout
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val subjectName = intent?.getStringExtra(EXTRA_SUBJECT_NAME) ?: "Focus Session"
        val endTimeMs = intent?.getLongExtra(EXTRA_END_TIME_MS, 0L) ?: 0L
        val totalSeconds = intent?.getLongExtra(EXTRA_TOTAL_SECONDS, 25L * 60L) ?: (25L * 60L)

        val notification = buildOngoingNotification(subjectName, endTimeMs)
        startForeground(NOTIFICATION_ID, notification)

        startBackgroundMonitor(endTimeMs)

        return START_STICKY
    }

    private fun startBackgroundMonitor(endTimeMs: Long) {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                if (now >= endTimeMs) {
                    launch(Dispatchers.Main) {
                        TimerStateManager.onFinished(applicationContext)
                        stopSelf()
                    }
                    break
                }
                delay(1000L)
            }
        }
    }

    private fun buildOngoingNotification(subjectName: String, endTimeMs: Long): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            3002,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("⏳ Focus Session Active: $subjectName")
            .setContentText("Focus timer is counting down in background")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentIntent(pendingIntent)

        if (endTimeMs > System.currentTimeMillis()) {
            builder.setUsesChronometer(true)
                .setChronometerCountDown(true)
                .setWhen(endTimeMs)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Active Focus Session",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live ongoing countdown when studying in background"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        monitorJob?.cancel()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {}
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
