package com.lttrung.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lttrung.notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTaskNotificationChannel()

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setupDefaultButtonListener()
        setupCustomButtonListener()
        setupProgressButtonListener()

    }

    private fun setupProgressButtonListener() {
        viewBinding.showProgressNotification.setOnClickListener {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
                setContentText("Picture Download")
                setContentText("Download in progress")
                setSmallIcon(android.R.drawable.stat_sys_download)
                priority = NotificationCompat.PRIORITY_LOW
            }
            val PROGRESS_MAX = 100
            val PROGRESS_CURRENT = 0

            NotificationManagerCompat.from(this).apply {
                val notificationId = 2
                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
                notify(notificationId, builder.build())

                Toast.makeText(this@MainActivity, "Downloaded", Toast.LENGTH_SHORT).show()
                builder.setContentText("Download complete")
                    .setProgress(0, 0, false)
                notify(notificationId, builder.build())
            }
        }
    }

    private fun setupCustomButtonListener() {
        viewBinding.showCustomNotification.setOnClickListener {
            val notificationLayout = RemoteViews(packageName, R.layout.custom_notification)

            val customNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .build()

            with(NotificationManagerCompat.from(this)) {
                val notificationId = 1
                notify(notificationId, customNotification)
            }
        }
    }

    private fun setupDefaultButtonListener() {
        viewBinding.showNotification.setOnClickListener {
            // Tạo builder
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            val title = "Task done"
            val content = "Your task was done by another person"
            // Tạo intent cho việc click vào thông báo
            val tapNotificationIntent =
                Intent(this, ShowTaskNotificationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                tapNotificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val snoozeIntent = Intent(this, ShowTaskNotificationActivity::class.java).apply {
                action = ACTION_SNOOZE
                putExtra(EXTRA_NOTIFICATION_ID, 0)
            }
            val snoozePendingIntent = PendingIntent.getActivity(this, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)
            builder.setSmallIcon(R.drawable.ic_baseline_notifications_24)
                // Set priority từ android 7.1 trở xuống
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(title)
                .setContentText(content)
                // Set style cho title, content và image
                .setStyle(NotificationCompat.BigTextStyle().bigText("This is big text"))
                // Set intent cho thao tác click
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_notifications_24, "Snooze", snoozePendingIntent)
                // SetAutoCancel để khi click vào thông báo tự động biến mất
                .setAutoCancel(true)
            with(NotificationManagerCompat.from(this)) {
                val notificationId = 1
                notify(notificationId, builder.build())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTaskNotificationChannel() {
        val channelName = CHANNEL_ID
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channelDescription = "This channel notify task status"
        val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "com.lttrung.notification.TEST_CHANNEL"
        private const val EXTRA_NOTIFICATION_ID = "com.lttrung.notification.TEST_CHANNEL"
        private const val ACTION_SNOOZE = "com.lttrung.notification.ACTION_SNOOZE"

    }
}