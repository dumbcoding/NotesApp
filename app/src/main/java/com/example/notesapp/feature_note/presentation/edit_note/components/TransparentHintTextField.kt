package com.example.notesapp.feature_note.presentation.edit_note.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun TransparentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
    singleLine: Boolean = true,
    onFocusChange: (FocusState) -> Unit
) {
    Box(modifier = modifier){
        BasicTextField(
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle,
            value = text,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.fillMaxWidth().onFocusChanged { onFocusChange(it) }
        )
        if(isHintVisible) {
            Text(text = hint, style = textStyle, color = MaterialTheme.colorScheme.outline)
        }
    }
}