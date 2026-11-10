package com.ivzb.urgat.ui.addEditWeight

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.DatePicker
import com.ivzb.urgat.ui.components.DeleteButton
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.components.rememberDebouncedClick
import com.ivzb.urgat.ui.components.responsiveButtonWidth
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

enum class FieldId {
    Weight,
    Date,
}

@Composable
fun AddEditWeightScreen(
    viewModel: AddEditWeightViewModel,

    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditWeightScreen(
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
fun AddEditWeightScreen(
    uiState: AddEditWeightUiState,
    onIntent: (AddEditWeightIntent) -> Unit,
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
            AddEditWeightTopAppBar(
                uiState = uiState,
                onIntent = onIntent,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                LoadingState(
                    label = R.string.loading,
                    modifier = Modifier.weight(1f)
                )
            } else {
                AddEditWeightForm(
                    uiState = uiState,
                    onIntent = onIntent,
                    focusRequesters = focusRequesters,
                )
            }
        }
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)

        LaunchedEffect(userMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(snackbarText)
                onIntent(AddEditWeightIntent.UserMessageShown)
            }
        }
    }
}

@Composable
private fun AddEditWeightTopAppBar(
    uiState: AddEditWeightUiState,
    onIntent: (AddEditWeightIntent) -> Unit,
    onBack: () -> Unit,
) {
    TextTopAppBar(
        title = uiState.screenTitleResId,
        onBack = onBack,
        actionsContent = {
            if (uiState.withDelete) {
                DeleteButton(onClick = { onIntent(AddEditWeightIntent.DeleteWeight) })
            }
        },
    )
}

@Composable
private fun ColumnScope.AddEditWeightForm(
    uiState: AddEditWeightUiState,
    onIntent: (AddEditWeightIntent) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    var valueTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = uiState.value,
                selection = TextRange(uiState.value.length)
            )
        )
    }

    WeightRow(
        valueTextFieldValue = valueTextFieldValue,
        onWeightChange = {
            valueTextFieldValue = it.copy(
                selection = TextRange(it.text.length)
            )

            onIntent(AddEditWeightIntent.UpdateValue(it.text))
        },
        focusRequesters = focusRequesters,
    )

    Spacer(
        modifier = Modifier
            .height(dimensionResource(R.dimen.vertical_margin))
    )

    DateRow(
        date = uiState.date,
        onIntent = onIntent,
        focusRequesters = focusRequesters,
    )

    Spacer(
        modifier = Modifier
            .weight(1f)
    )

    SaveButton(
        onIntent = onIntent,
    )

    LaunchedEffect(uiState.focusIndex) {
        focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
    }
}

@Composable
private fun WeightRow(
    valueTextFieldValue: TextFieldValue,
    onWeightChange: (TextFieldValue) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    TextField(
        value = valueTextFieldValue,
        onValueChange = onWeightChange,
        placeholder = { Text(text = stringResource(id = R.string.weight_hint)) },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequesters[FieldId.Weight.ordinal]),
    )
}

@Composable
private fun DateRow(
    date: Long,
    onIntent: (AddEditWeightIntent) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    val focusManager = LocalFocusManager.current

    DatePicker(
        timestamp = date,
        onTimestampSelected = {
            onIntent(AddEditWeightIntent.UpdateDate(it))
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
}

@Composable
private fun SaveButton(
    onIntent: (AddEditWeightIntent) -> Unit,
) {
    Button(
        onClick = rememberDebouncedClick {
            onIntent(AddEditWeightIntent.SaveWeight)
        },
        modifier = Modifier
            .responsiveButtonWidth()
    ) {
        Text(stringResource(R.string.save))
    }
}

@AllDevicesPreview
@Composable
private fun AddWeightScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditWeightScreen(
                uiState = AddEditWeightUiState(
                    screenTitleResId = R.string.add_weight,
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
private fun EditWeightScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditWeightScreen(
                uiState = AddEditWeightUiState(
                    screenTitleResId = R.string.edit_weight,
                    value = "80.5",
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
private fun LoadingWeightScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditWeightScreen(
                uiState = AddEditWeightUiState(
                    screenTitleResId = R.string.edit_weight,
                    isLoading = true,
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}
