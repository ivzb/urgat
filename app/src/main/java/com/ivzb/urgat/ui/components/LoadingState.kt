package com.ivzb.urgat.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun LoadingState(
    @StringRes label: Int,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator()

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.list_item_padding))
        )

        Text(
            text = stringResource(label)
        )
    }
}

@Preview
@Composable
private fun LoadingStatePreview() {
    UrgatTheme {
        Surface {
            LoadingState(
                label = R.string.loading_todos,
            )
        }
    }
}
