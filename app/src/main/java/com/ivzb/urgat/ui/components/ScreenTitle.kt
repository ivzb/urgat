package com.ivzb.urgat.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.R

@Composable
fun ScreenTitleWithInfo(
    title: String,
    dialogTitle: String,
    dialogText: String,
    modifier: Modifier = Modifier
) {

    var showInfo by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { showInfo = true }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.info)
            )
        }
    }

    if (showInfo) {
        InfoDialog(
            title = dialogTitle,
            text = dialogText,
            onDismiss = { showInfo = false },
        )
    }
}
