package com.ivzb.urgat.ui.addEditExpense

import androidx.annotation.StringRes
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
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

enum class FieldId {
    Name,
    Amount,
    Category,
    Date,
}

@Composable
fun AddEditExpenseScreen(
    @StringRes screenTitle: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditExpenseContent(
        screenTitle = screenTitle,
        onBack = onBack,
        uiState = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )

    // Check if the task is saved and call onTaskUpdate event
    LaunchedEffect(uiState.isExpenseSaved) {
        if (uiState.isExpenseSaved) {
            onBack()
        }
    }
}

@Composable
private fun AddEditExpenseContent(
    screenTitle: Int,
    onBack: () -> Unit,
    uiState: AddEditExpenseUiState,
    onIntent: (AddEditExpenseIntent) -> Unit,
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
            TextTopAppBar(screenTitle, onBack)
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
                Text("Loading...")
            } else {
                var nameTextFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = uiState.name,
                            selection = TextRange(uiState.name.length)
                        )
                    )
                }

                TextField(
                    value = nameTextFieldValue,
                    onValueChange = {
                        nameTextFieldValue = it.copy(
                            selection = TextRange(it.text.length)
                        )

                        onIntent(AddEditExpenseIntent.UpdateName(it.text))
                    },
                    placeholder = { Text(text = stringResource(id = R.string.expense_hint)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesters[FieldId.Name.ordinal]),
                )

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                TextField(
                    value = uiState.amount,
                    onValueChange = {
                        onIntent(AddEditExpenseIntent.UpdateAmount(it))
                    },
                    placeholder = { Text(stringResource(id = R.string.amount_hint)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesters[FieldId.Amount.ordinal]),
                )

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
                        value = uiState.category,
                        onValueChange = {
                            onIntent(AddEditExpenseIntent.UpdateCategory(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.category_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[FieldId.Category.ordinal]),
                    )

                    DatePicker(
                        timestamp = uiState.date,
                        onTimestampSelected = {
                            onIntent(AddEditExpenseIntent.UpdateDate(it))
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
                        .weight(1f)
                )

                Button(
                    onClick = {
                        onIntent(AddEditExpenseIntent.SaveExpense)
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
                onIntent(AddEditExpenseIntent.UserMessageShown)
            }
        }
    }
}

@Preview
@Composable
private fun AddContentPreview() {
    UrgatTheme {
        Surface {
            AddEditExpenseContent(
                screenTitle = R.string.add_expense,
                onBack = {},
                uiState = AddEditExpenseUiState(),
                onIntent = {},
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
private fun EditExpenseContentPreview() {
    UrgatTheme {
        Surface {
            AddEditExpenseContent(
                screenTitle = R.string.edit_expense,
                onBack = {},
                uiState = AddEditExpenseUiState(
                    name = "Title",
                    amount = "12.3",
                    category = "",
                    date = 0L,
                ),
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
            AddEditExpenseContent(
                screenTitle = R.string.add_expense,
                onBack = {},
                uiState = AddEditExpenseUiState(
                    isLoading = true
                ),
                onIntent = {},
                modifier = Modifier
            )
        }
    }
}