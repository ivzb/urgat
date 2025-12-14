package com.ivzb.urgat.ui.components

import androidx.annotation.StringRes
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun FloatingActionButtonContent(
    extended: Boolean,
    icon: ImageVector,
    @StringRes
    labelResId: Int,
    onClick: () -> Unit,
) {
    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )

            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(labelResId),
                    modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}

@Preview
@DarkModePreview
@Composable
fun FloatingActionButtonContentCollapsedPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            extended = false,
            icon = Icons.Filled.Add,
            labelResId = R.string.add,
            onClick = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun FloatingActionButtonContentExtendedPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            extended = true,
            icon = Icons.Filled.Add,
            labelResId = R.string.add,
            onClick = {},
        )
    }
}
