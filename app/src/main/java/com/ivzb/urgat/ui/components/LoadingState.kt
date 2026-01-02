package com.ivzb.urgat.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.canvas3d.Canvas3d
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun LoadingState(
    @StringRes label: Int,
    vararg formatArgs: Any,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Canvas3d(
            text = stringResource(label, formatArgs = formatArgs),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.loading_canvas_3d_padding))
        )
    }
}

@AllDevicesPreview
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
