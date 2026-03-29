package com.ivzb.urgat.ui.addEditWatchlistAsset

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.DeleteButton
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.components.rememberDebouncedClick
import com.ivzb.urgat.ui.components.responsiveButtonWidth
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

enum class FieldId {
    Name,
}

@Composable
fun AddEditWatchlistAssetScreen(
    viewModel: AddEditWatchlistAssetViewModel,

    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditWatchlistAssetScreen(
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
fun AddEditWatchlistAssetScreen(
    uiState: AddEditWatchlistAssetUiState,
    onIntent: (AddEditWatchlistAssetIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequesters = remember {
        List(FieldId.entries.size) { FocusRequester() }
    }

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = uiState.screenTitleResId,
                onBack = onBack,
                actionsContent = {
                    if (uiState.withDelete) {
                        DeleteButton(onClick = { onIntent(AddEditWatchlistAssetIntent.DeleteWatchlistAsset) })
                    }
                },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                LoadingState(
                    label = R.string.loading,
                    modifier = Modifier.weight(1f)
                )
            } else {
                var nameTextFieldValue by remember { mutableStateOf(TextFieldValue(uiState.name)) }

                TextField(
                    value = nameTextFieldValue,
                    onValueChange = {
                        nameTextFieldValue = it
                        onIntent(AddEditWatchlistAssetIntent.UpdateName(it.text))
                    },
                    placeholder = { Text(text = stringResource(id = R.string.watchlist_asset_name_hint)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesters[FieldId.Name.ordinal]),
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Button(
                    onClick = rememberDebouncedClick {
                        onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)
                    },
                    modifier = Modifier
                        .responsiveButtonWidth()
                ) {
                    Text(stringResource(R.string.save))
                }

                LaunchedEffect(uiState.focusIndex) {
                    focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
                }

                LaunchedEffect(uiState.isLoading) {
                    nameTextFieldValue = TextFieldValue(
                        text = uiState.name,
                        selection = TextRange(uiState.name.length)
                    )

                    focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
                }
            }
        }
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)

        LaunchedEffect(userMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(snackbarText)
                onIntent(AddEditWatchlistAssetIntent.UserMessageShown)
            }
        }
    }
}

@AllDevicesPreview
@Composable
private fun AddWatchlistAssetScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditWatchlistAssetScreen(
                uiState = AddEditWatchlistAssetUiState(
                    screenTitleResId = R.string.add_watchlist_asset,
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}

@AllDevicesPreview
@Composable
private fun EditWatchlistAssetContentPreview() {
    UrgatTheme {
        Surface {
            AddEditWatchlistAssetScreen(
                uiState = AddEditWatchlistAssetUiState(
                    screenTitleResId = R.string.edit_watchlist_asset,
                    name = "AAPL",
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}

@AllDevicesPreview
@Composable
private fun LoadingContentPreview() {
    UrgatTheme {
        Surface {
            AddEditWatchlistAssetScreen(
                uiState = AddEditWatchlistAssetUiState(
                    screenTitleResId = R.string.edit_watchlist_asset,
                    isLoading = true,
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}