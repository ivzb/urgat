package com.ivzb.urgat.ui.sample

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
fun SampleScreen(
    viewModel: SampleViewModel,

    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SampleScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier
    )

    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            onBack()
        }
    }
}

@VisibleForTesting
@Composable
fun SampleScreen(
    uiState: SampleUiState,
    onIntent: (SampleIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = R.string.sample_data,
                onBack = onBack,
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
                        onClick = { onIntent(SampleIntent.AddSampleData) },
                    ) {

                        Text(stringResource(R.string.add_sample_data))
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
                onIntent(SampleIntent.UserMessageShown)
            }
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun SampleScreenPreview() {
    UrgatTheme {
        SampleScreen(
            uiState = SampleUiState(
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
private fun SampleScreenLoadingPreview() {
    UrgatTheme {
        SampleScreen(
            uiState = SampleUiState(
                isLoading = true,
            ),
            onIntent = {},
            onBack = {},
            modifier = Modifier,
        )
    }
}
