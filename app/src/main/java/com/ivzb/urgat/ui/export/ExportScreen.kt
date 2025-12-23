package com.ivzb.urgat.ui.export

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.DarkModePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

@Composable
fun ExportScreen(
    viewModel: ExportViewModel,

    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExportScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier
    )
}


@VisibleForTesting
@Composable
fun ExportScreen(
    uiState: ExportUiState,
    onIntent: (ExportIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = R.string.export_data,
                onBack = onBack,
                withDelete = false,
                onDelete = {},
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

            when {
                uiState.isLoading -> {
                    LoadingState(
                        label = R.string.loading,
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    Button(
                        onClick = { onIntent(ExportIntent.ExportData) },
                    ) {

                        Text(stringResource(R.string.export_data))
                    }
                }
            }
        }
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)

        LaunchedEffect(userMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(snackbarText)
                onIntent(ExportIntent.UserMessageShown)
            }
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun ExportScreenPreview() {
    UrgatTheme {
        ExportScreen(
            uiState = ExportUiState(
                isLoading = false,
            ),
            onIntent = {},
            onBack = {},
            modifier = Modifier,
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ExportScreenLoadingPreview() {
    UrgatTheme {
        ExportScreen(
            uiState = ExportUiState(
                isLoading = true,
            ),
            onIntent = {},
            onBack = {},
            modifier = Modifier,
        )
    }
}
