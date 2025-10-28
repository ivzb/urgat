package com.ivzb.urgat.ui.addEditTodo

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.transparentTextFieldColors
import kotlinx.coroutines.launch

@Composable
fun AddEditTodoScreen(
    @StringRes screenTitle: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditTodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditTodoContent(
        screenTitle = screenTitle,
        onBack = onBack,
        onSave = viewModel::saveTodo,
        loading = uiState.isLoading,
        userMessage = uiState.userMessage,
        onShowUserMessage = {
            viewModel.snackbarMessageShown()
        },
        title = uiState.title,
        description = uiState.description,
        onTitleChanged = viewModel::updateTitle,
        onDescriptionChanged = viewModel::updateDescription,
        modifier = modifier
    )

    // Check if the task is saved and call onTaskUpdate event
    LaunchedEffect(uiState.isTodoSaved) {
        if (uiState.isTodoSaved) {
            onBack()
        }
    }
}

@Composable
private fun AddEditTodoContent(
    screenTitle: Int,
    onBack: () -> Unit,
    onSave: () -> Unit,
    loading: Boolean,
    userMessage: Int?,
    onShowUserMessage: () -> Unit,
    title: String,
    description: String,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
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
                var titleTextFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = title,
                            selection = TextRange(title.length)
                        )
                    )
                }

                TextField(
                    value = titleTextFieldValue,
                    onValueChange = {
                        titleTextFieldValue = it.copy(
                            selection = TextRange(it.text.length)
                        )

                        onTitleChanged(it.text)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_hint),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    colors = transparentTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                TextField(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    placeholder = { Text(stringResource(id = R.string.description_hint)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    colors = transparentTextFieldColors(),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
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
            AddEditTodoContent(
                screenTitle = R.string.add_todo,
                onBack = {},
                onSave = {},
                loading = false,
                userMessage = null,
                onShowUserMessage = {},
                title = "",
                description = "",
                onTitleChanged = {},
                onDescriptionChanged = {},
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
private fun EditTodoContentPreview() {
    UrgatTheme {
        Surface {
            AddEditTodoContent(
                screenTitle = R.string.edit_todo,
                onBack = {},
                onSave = {},
                loading = false,
                userMessage = null,
                onShowUserMessage = {},
                title = "Title",
                description = "Description",
                onTitleChanged = {},
                onDescriptionChanged = {},
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
            AddEditTodoContent(
                screenTitle = R.string.add_todo,
                onBack = {},
                onSave = {},
                loading = true,
                userMessage = null,
                onShowUserMessage = {},
                title = "",
                description = "",
                onTitleChanged = {},
                onDescriptionChanged = {},
                modifier = Modifier
            )
        }
    }
}
