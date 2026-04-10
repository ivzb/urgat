package com.ivzb.urgat.ui.inAppUpdate

import android.app.Activity
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R

@Composable
fun InAppUpdateSnackbarHost(
    viewModel: InAppUpdateViewModel,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? Activity

    val updateAvailableMessage = stringResource(R.string.in_app_update_available)
    val updateAction = stringResource(R.string.in_app_update_action)
    val downloadedMessage = stringResource(R.string.in_app_update_downloaded)
    val restartAction = stringResource(R.string.in_app_update_restart)
    val downloadingMessage = stringResource(R.string.in_app_update_downloading)

    LaunchedEffect(uiState.isUpdateAvailable, uiState.isDismissed) {
        if (uiState.isUpdateAvailable && !uiState.isDismissed) {
            val result = snackbarHostState.showSnackbar(
                message = updateAvailableMessage,
                actionLabel = updateAction,
                duration = SnackbarDuration.Long,
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    activity?.let { viewModel.startUpdate(it) }
                }

                SnackbarResult.Dismissed -> {
                    viewModel.onIntent(InAppUpdateIntent.DismissUpdate)
                }
            }
        }
    }

    LaunchedEffect(uiState.isDownloading) {
        if (uiState.isDownloading) {
            snackbarHostState.showSnackbar(
                message = downloadingMessage,
                duration = SnackbarDuration.Short,
            )
        }
    }

    LaunchedEffect(uiState.isDownloaded) {
        if (uiState.isDownloaded) {
            val result = snackbarHostState.showSnackbar(
                message = downloadedMessage,
                actionLabel = restartAction,
                duration = SnackbarDuration.Indefinite,
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onIntent(InAppUpdateIntent.CompleteUpdate)
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
    ) { data ->
        Snackbar(snackbarData = data)
    }
}


