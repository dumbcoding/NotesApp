package com.example.notesapp.feature_note.presentation.edit_note

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.notesapp.feature_note.presentation.edit_note.components.TransparentHintTextField
import com.example.notesapp.feature_note.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditNoteScreen(
    navController: NavController,
    viewModel: EditNoteViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy");
    val dateTime = if(viewModel.timestamp.value == (-1).toLong()){
        "Created now"
    }else {
        simpleDateFormat.format(Date(viewModel.timestamp.value))
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is EditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
                is EditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }

            }
        }
    }

    Scaffold(
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp - 30.dp)) {
                FloatingActionButton(onClick = {
                     navController.navigate(Screen.NotificationScreen.route.replace("{noteTitle}", titleState.text))
                }, containerColor = MaterialTheme.colorScheme.primary) {
                    Icon(imageVector = Icons.Default.Alarm, contentDescription = "Create a reminder")
                }
                FloatingActionButton(onClick = {
                    viewModel.OnEvent(EditNoteEvent.SaveNote)
                }, containerColor = MaterialTheme.colorScheme.primary) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save note")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = dateTime, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = {viewModel.OnEvent(EditNoteEvent.EnteredTitle(it))},
                onFocusChange = {viewModel.OnEvent(EditNoteEvent.ChangeTitleFocus(it))},
                isHintVisible = titleState.isHintVisible,
                singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                text = contentState.text,
                hint = contentState.hint,
                onValueChange = {viewModel.OnEvent(EditNoteEvent.EnteredContent(it))},
                onFocusChange = {viewModel.OnEvent(EditNoteEvent.ChangeContentFocus(it))},
                isHintVisible = contentState.isHintVisible,
                singleLine = false,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}
