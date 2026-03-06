package com.ivzb.urgat.ui.about

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.DarkModePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun AboutScreen(
    viewModel: AboutViewModel,

    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AboutScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier
    )
}


@VisibleForTesting
@Composable
fun AboutScreen(
    uiState: AboutUiState,
    onIntent: (AboutIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = R.string.about,
                onBack = onBack,
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(
                    horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                    vertical = dimensionResource(R.dimen.content_padding_vertical),
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            LoadingState(
                label = R.string.version_format,
                formatArgs = arrayOf(BuildConfig.VERSION_NAME),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun ExportScreenPreview() {
    UrgatTheme {
        AboutScreen(
            uiState = AboutUiState,
            onIntent = {},
            onBack = {},
            modifier = Modifier,
        )
    }
}
