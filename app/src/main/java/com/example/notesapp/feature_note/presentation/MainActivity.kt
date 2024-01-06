package com.example.notesapp.feature_note.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.notesapp.feature_note.presentation.add_notification.NotificationScreen
import com.example.notesapp.ui.theme.NotesAppTheme
import com.example.notesapp.feature_note.presentation.edit_note.EditNoteScreen
import com.example.notesapp.feature_note.presentation.note.NotesScreen
import com.example.notesapp.feature_note.presentation.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = {}
                    )
                    var hasNotificationPermission by remember {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                            mutableStateOf(
                                ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            )
                        } else mutableStateOf(true)
                    }
                    var hasAlarmNotificationPermission by remember {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                            mutableStateOf(
                                ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.SCHEDULE_EXACT_ALARM
                                ) == PackageManager.PERMISSION_GRANTED
                            )
                        } else mutableStateOf(true)
                    }

                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.NotesScreen.route){
                        composable(route = Screen.NotesScreen.route){
                            NotesScreen(navController = navController)
                        }
                        composable(route = Screen.NotificationScreen.route){backStackEntry->
                            val noteTitle =  backStackEntry.arguments?.getString("noteTitle")
                            NotificationScreen(navController = navController, noteTitle = noteTitle)
                        }
                        composable(
                            route = Screen.EditNoteScreen.route + "?noteId={noteId}",
                            arguments = listOf(
                                navArgument(
                                    name = "noteId"
                                ){
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ){
                            EditNoteScreen(navController = navController)
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                                if (!hasNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                if (!hasAlarmNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}