package com.ivzb.urgat.ui.trackWeight

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.DatePicker
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.components.rememberDebouncedClick
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

enum class FieldId {
    Weight,
    Date,
}

@Composable
fun TrackWeightScreen(
    @StringRes screenTitle: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackWeightViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrackWeightContent(
        screenTitle = screenTitle,
        onBack = onBack,
        uiState = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )

    LaunchedEffect(uiState.isWeightSaved) {
        if (uiState.isWeightSaved) {
            onBack()
        }
    }
}

@Composable
private fun TrackWeightContent(
    screenTitle: Int,
    onBack: () -> Unit,
    uiState: TrackWeightUiState,
    onIntent: (TrackWeightIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequesters = remember {
        List(FieldId.entries.size) { FocusRequester() }
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = screenTitle,
                onBack = onBack,
                withDelete = false,
                onDelete = {
                    // NOOP
                }
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
                .padding(dimensionResource(R.dimen.content_padding))
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading) {
                LoadingState(
                    label = R.string.loading,
                    modifier = Modifier.weight(1f)
                )
            } else {
                var valueTextFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = uiState.value,
                            selection = TextRange(uiState.value.length)
                        )
                    )
                }

                TextField(
                    value = valueTextFieldValue,
                    onValueChange = {
                        valueTextFieldValue = it.copy(
                            selection = TextRange(it.text.length)
                        )

                        onIntent(TrackWeightIntent.UpdateValue(it.text))
                    },
                    placeholder = { Text(text = stringResource(id = R.string.weight_hint)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesters[FieldId.Weight.ordinal]),
                )

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                DatePicker(
                    timestamp = uiState.date,
                    onTimestampSelected = {
                        onIntent(TrackWeightIntent.UpdateDate(it))
                    }
                ) { selectedDate, dialog ->

                    TextField(
                        value = selectedDate,
                        onValueChange = {},
                        placeholder = { Text(stringResource(id = R.string.date_hint)) },
                        label = { Text(stringResource(id = R.string.date_hint)) },
                        readOnly = true,
                        maxLines = 1,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(id = R.string.date_hint)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequesters[FieldId.Date.ordinal])
                            .onFocusChanged {
                                if (it.isFocused) {
                                    dialog.show()
                                    focusManager.clearFocus()
                                }
                            },
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Button(
                    onClick = rememberDebouncedClick {
                        onIntent(TrackWeightIntent.SaveWeight)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }

                LaunchedEffect(uiState.focusIndex) {
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
                onIntent(TrackWeightIntent.UserMessageShown)
            }
        }
    }
}

@Preview
@Composable
private fun AddContentPreview() {
    UrgatTheme {
        Surface {
            TrackWeightContent(
                screenTitle = R.string.track_weight,
                onBack = {},
                uiState = TrackWeightUiState(),
                onIntent = {},
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
private fun LoadingContentPreview() {
    UrgatTheme {
        Surface {
            TrackWeightContent(
                screenTitle = R.string.track_weight,
                onBack = {},
                uiState = TrackWeightUiState(
                    isLoading = true
                ),
                onIntent = {},
                modifier = Modifier
            )
        }
    }
}