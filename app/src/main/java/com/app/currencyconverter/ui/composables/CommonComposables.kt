package com.app.currencyconverter.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.app.currencyconverter.R

@Composable
fun CommonAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onDismissClick: () -> Unit = onDismissRequest,
    dialogTitle: String,
    dialogText: String,
    icon: Painter? = painterResource(R.drawable.ic_error),
    confirmText: String = stringResource(id = android.R.string.ok),
    dismissText: String? = stringResource(id = android.R.string.cancel),
) {
    AlertDialog(
        icon = {
            icon?.let {
                Icon(it, contentDescription = "Example Icon")
            }
        },
        title = {
            Text(text = dialogTitle, fontSize = 16.sp)
        },
        text = {
            Column {
                Text(text = dialogText, fontSize = 14.sp)
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            dismissText?.let {
                TextButton(
                    onClick = {
                        onDismissClick.invoke()
                    }
                ) {
                    Text(it)
                }
            }
        }
    )
}