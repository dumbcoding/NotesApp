package com.example.notesapp.feature_note.presentation.add_notification

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.notesapp.R

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "title"
const val messageExtra = "message"

class Notification: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification: Notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.alarm_notification)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}