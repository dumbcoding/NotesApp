package com.example.notesapp.feature_note.presentation.add_notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.util.Calendar
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    noteTitle: String?
) {
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableYear(year: Int): Boolean {
                return year > 2023
            }
        },
        initialDisplayMode = DisplayMode.Input
    )
    val timePickerState = rememberTimePickerState()
    var dateMillis = mutableLongStateOf(
        (datePickerState.selectedDateMillis ?: 0) + (timePickerState.hour * 3600 * 1000).toLong() + (timePickerState.minute * 60 * 1000).toLong() + getZoneOffset().toLong()
    )

    createNotificationChannel(context)
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (noteTitle != null && hasNotificationPermission) {
                    scheduleNotification(context, noteTitle, dateMillis.value)
                }
                navController.navigateUp()
            }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Schedule notification")
            }
        }
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create a notification",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close the section"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            DatePicker(
                state = datePickerState, showModeToggle = false, modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TimePicker(
                state = timePickerState, modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


private fun createNotificationChannel(context: Context) {
    val name = "Notes Scheduled Notifications"
    val description = "Sends you a reminder that you've scheduled"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(channelID, name, importance)
    channel.description = description
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}


private fun scheduleNotification(context: Context, noteTitle: String, timeMillis: Long){
    val intent = Intent(context, Notification::class.java)
    val title = noteTitle
    val message = "You've scheduled this notification for «$noteTitle» note."
    intent.putExtra(titleExtra, title)
    intent.putExtra(messageExtra, message)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationID,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timeMillis,
        pendingIntent
    )
}

private fun getZoneOffset(): Int {
    val calendar = Calendar.getInstance(Locale.getDefault());
    return -(calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET))
}