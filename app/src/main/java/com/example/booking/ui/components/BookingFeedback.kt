package com.example.booking.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun BookingMapNoticeDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Map is unavailable in this demo")
        },
        text = {
            Text(text = "The Map action is still recorded locally so your flow can be detected.")
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Got it")
            }
        }
    )
}
