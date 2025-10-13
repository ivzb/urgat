package com.ivzb.urgat.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun FloatingActionButtonContent(
    onAddItem: () -> Unit
) {
    FloatingActionButton(onClick = {
        onAddItem()
    }) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.new_todo)
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonContentPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            onAddItem = {}
        )
    }
}
