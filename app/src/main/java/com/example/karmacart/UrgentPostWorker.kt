package com.example.karmacart

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class UrgentPostWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val title = inputData.getString("title") ?: "Urgent request"
        val category = inputData.getString("category") ?: ""

        showNotification(title, category)

        return Result.success()
    }

    private fun showNotification(title: String, category: String) {

        val channelId = "urgent_posts"

        // Create notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Urgent Requests",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // SAFE icon
            .setContentTitle("Urgent request")
            .setContentText("$title • $category")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}
