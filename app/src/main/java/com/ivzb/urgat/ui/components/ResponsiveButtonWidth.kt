package com.ivzb.urgat.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import com.ivzb.urgat.R

@Composable
fun Modifier.responsiveButtonWidth(): Modifier {
    val configuration = LocalConfiguration.current

    return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        this.width(dimensionResource(R.dimen.button_widget))
    } else {
        this.fillMaxWidth() // portrait width
    }
}