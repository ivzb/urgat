package com.ivzb.urgat.ui.addEditExpense

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

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
        onSave = viewModel::saveExpense,
        loading = uiState.isLoading,
        userMessage = uiState.userMessage,
        onShowUserMessage = {
            viewModel.snackbarMessageShown()
        },
        name = uiState.name,
        amount = uiState.amount,
        onNameChanged = viewModel::updateName,
        onAmountChanged = viewModel::updateAmount,
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
    onSave: () -> Unit,
    loading: Boolean,
    userMessage: Int?,
    onShowUserMessage: () -> Unit,
    name: String,
    amount: String,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

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
            if (loading) {
                Text("Loading...")
            } else {
                var nameTextFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = name,
                            selection = TextRange(name.length)
                        )
                    )
                }

                TextField(
                    value = nameTextFieldValue,
                    onValueChange = {
                        nameTextFieldValue = it.copy(
                            selection = TextRange(it.text.length)
                        )

                        onNameChanged(it.text)
                    },
                    placeholder = { Text(text = stringResource(id = R.string.expense_hint)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                TextField(
                    value = amount,
                    onValueChange = onAmountChanged,
                    placeholder = { Text(stringResource(id = R.string.amount_hint)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)

        LaunchedEffect(userMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(snackbarText)
                onShowUserMessage()
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
                onSave = {},
                loading = false,
                userMessage = null,
                onShowUserMessage = {},
                name = "",
                amount = "",
                onNameChanged = {},
                onAmountChanged = {},
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
                onSave = {},
                loading = false,
                userMessage = null,
                onShowUserMessage = {},
                name = "Title",
                amount = "12.3",
                onNameChanged = {},
                onAmountChanged = {},
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
                onSave = {},
                loading = true,
                userMessage = null,
                onShowUserMessage = {},
                name = "",
                amount = "",
                onNameChanged = {},
                onAmountChanged = {},
                modifier = Modifier
            )
        }
    }
}