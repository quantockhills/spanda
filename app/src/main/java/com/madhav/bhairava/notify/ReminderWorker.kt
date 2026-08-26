package com.madhav.bhairava.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.madhav.bhairava.MainActivity
import com.madhav.bhairava.R
import com.madhav.bhairava.data.Repository
import kotlin.random.Random

class ReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val lib = Repository.library(context)
        val pool = buildPool(lib)
        if (pool.isEmpty()) return Result.success()

        val prefs = context.getSharedPreferences(ReminderScheduler.PREFS, Context.MODE_PRIVATE)
        val last = prefs.getInt("last_index", -1)
        var idx = Random.nextInt(pool.size)
        if (pool.size > 1 && idx == last) idx = (idx + 1) % pool.size
        prefs.edit().putInt("last_index", idx).apply()

        showNotification(context, pool[idx])
        return Result.success()
    }

    private fun showNotification(context: Context, m: Meditation) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                ReminderScheduler.CHANNEL_ID,
                context.getString(R.string.channel_daily),
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", m.route)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            context, m.route.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val body = m.notificationBody()
        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(m.title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${m.sanskrit}\n\n$body"))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
}
