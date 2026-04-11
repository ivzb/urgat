package com.ivzb.urgat.worker

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
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ivzb.urgat.MainActivity
import com.ivzb.urgat.R
import com.ivzb.urgat.data.weight.WeightDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeightReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val weightDao: WeightDao,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val latestDate = weightDao.getLatestDate()

        val twoWeeksMs = 14L * 24 * 60 * 60 * 1000
        val now = System.currentTimeMillis()

        val shouldRemind = latestDate == null || (now - latestDate) >= twoWeeksMs

        if (shouldRemind) {
            showNotification()
        }

        return Result.success()
    }

    private fun showNotification() {
        val context = applicationContext

        createNotificationChannel(context)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.weight_reminder_title))
            .setContentText(context.getString(R.string.weight_reminder_message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.weight_reminder_channel_name)
        val descriptionText = context.getString(R.string.weight_reminder_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {

        const val CHANNEL_ID = "weight_reminder_channel"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "weight_reminder_work"

    }
}

