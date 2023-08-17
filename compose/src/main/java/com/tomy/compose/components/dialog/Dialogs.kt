package com.tomy.compose.components.dialog

import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifyDialog(
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    show: MutableState<Boolean>
) {
    if (show.value) {
        AlertDialog(
            modifier = Modifier.background(
                Color.Green
            ),
            onDismissRequest = {
                Timber.d("onDismiss")
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Button(onClick = { show.value = false }) {
                Text(text = "Message")
            }
        }
    }
}