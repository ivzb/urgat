package com.ivzb.urgat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun FloatingActionButtonContent(
    extended: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(R.string.add),
                    modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun FloatingActionButtonContentPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            extended = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonContentExtendedPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            extended = true,
            onClick = {}
        )
    }
}
