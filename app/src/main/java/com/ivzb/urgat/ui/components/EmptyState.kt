package com.ivzb.urgat.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun EmptyState(
    @StringRes label: Int,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )

        Text(stringResource(id = label))
    }
}

@Preview
@Composable
private fun EmptyTodosPreview() {
    UrgatTheme {
        Surface {
            EmptyState(
                label = R.string.no_todos,
                iconRes = R.drawable.logo_no_fill,
            )
        }
    }
}

@Preview
@Composable
private fun LoadingTodosPreview() {
    UrgatTheme {
        Surface {
            EmptyState(
                label = R.string.loading_todos,
                iconRes = R.drawable.ic_loading,
            )
        }
    }
}

@Preview
@Composable
private fun ErrorTodosPreview() {
    UrgatTheme {
        Surface {
            EmptyState(
                label = R.string.error_todos,
                iconRes = R.drawable.ic_error,
            )
        }
    }
}