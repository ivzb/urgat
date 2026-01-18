package com.ivzb.urgat.ui.addEditBudget

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.ivzb.urgat.ui.components.CheckboxWithLabel
import com.ivzb.urgat.ui.components.DatePicker
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.components.rememberDebouncedClick
import com.ivzb.urgat.ui.components.responsiveButtonWidth
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

enum class FieldId {
    Income,
    BankStart,
    BankEnd,
    Date,
}

@Composable
fun AddEditBudgetScreen(
    viewModel: AddEditBudgetViewModel,

    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditBudgetScreen(
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
fun AddEditBudgetScreen(
    uiState: AddEditBudgetUiState,
    onIntent: (AddEditBudgetIntent) -> Unit,
    onBack: () -> Unit,
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
                title = uiState.screenTitleResId,
                onBack = onBack,
                withDelete = uiState.withDelete,
                onDelete = { onIntent(AddEditBudgetIntent.DeleteBudget) },
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
                var incomeTextFieldValue by remember { mutableStateOf(TextFieldValue(uiState.income)) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
                ) {
                    TextField(
                        value = incomeTextFieldValue,
                        onValueChange = {
                            incomeTextFieldValue = it
                            onIntent(AddEditBudgetIntent.UpdateIncome(it.text))
                        },
                        placeholder = { Text(text = stringResource(id = R.string.income_hint)) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[FieldId.Income.ordinal]),
                    )

                    DatePicker(
                        timestamp = uiState.date,
                        onTimestampSelected = {
                            onIntent(AddEditBudgetIntent.UpdateDate(it))
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
                                .weight(1f)
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

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
                ) {
                    TextField(
                        value = uiState.bankStart,
                        onValueChange = {
                            onIntent(AddEditBudgetIntent.UpdateBankStart(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.bank_start_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[FieldId.BankStart.ordinal]),
                    )

                    TextField(
                        value = uiState.bankEnd,
                        onValueChange = {
                            onIntent(AddEditBudgetIntent.UpdateBankEnd(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.bank_end_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[FieldId.BankEnd.ordinal]),
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Button(
                    onClick = rememberDebouncedClick {
                        onIntent(AddEditBudgetIntent.SaveBudget)
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
                    incomeTextFieldValue = TextFieldValue(
                        text = uiState.income,
                        selection = TextRange(uiState.income.length)
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
                onIntent(AddEditBudgetIntent.UserMessageShown)
            }
        }
    }
}

@AllDevicesPreview
@Composable
private fun AddBudgetScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditBudgetScreen(
                uiState = AddEditBudgetUiState(
                    screenTitleResId = R.string.add_budget,
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
private fun EditBudgetScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditBudgetScreen(
                uiState = AddEditBudgetUiState(
                    screenTitleResId = R.string.edit_budget,
                    income = "5000",
                    bankStart = "9900",
                    bankEnd = "11800",
                    date = 0L,
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
private fun LoadingBudgetScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditBudgetScreen(
                uiState = AddEditBudgetUiState(
                    screenTitleResId = R.string.edit_budget,
                    isLoading = true
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}