package com.ivzb.urgat.ui.addEditNote

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
import com.ivzb.urgat.util.transparentTextFieldColors
import kotlinx.coroutines.launch

enum class FieldId {
    Content,
}

@Composable
fun AddEditNoteScreen(
    @StringRes screenTitle: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditNoteContent(
        screenTitle = screenTitle,
        onBack = onBack,
        onSave = viewModel::saveNote,
        loading = uiState.isLoading,
        userMessage = uiState.userMessage,
        onShowUserMessage = {
            viewModel.snackbarMessageShown()
        },
        content = uiState.content,
        focusIndex = uiState.focusIndex,
        onContentChanged = viewModel::updateContent,
        modifier = modifier
    )

    // Check if the task is saved and call onTaskUpdate event
    LaunchedEffect(uiState.isNoteSaved) {
        if (uiState.isNoteSaved) {
            onBack()
        }
    }
}

@Composable
private fun AddEditNoteContent(
    screenTitle: Int,
    onBack: () -> Unit,
    onSave: () -> Unit,
    loading: Boolean,
    userMessage: Int?,
    onShowUserMessage: () -> Unit,
    content: String,
    focusIndex: Int?,
    onContentChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequesters = remember {
        List(FieldId.entries.size) { FocusRequester() }
    }

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
                var contentTextFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = content,
                            selection = TextRange(content.length)
                        )
                    )
                }

                TextField(
                    value = contentTextFieldValue,
                    onValueChange = {
                        contentTextFieldValue = it.copy(
                            selection = TextRange(it.text.length)
                        )

                        onContentChanged(it.text)
                    },
                    placeholder = { Text(text = stringResource(id = R.string.note_hint)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    colors = transparentTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .focusRequester(focusRequesters[FieldId.Content.ordinal]),
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

                LaunchedEffect(focusIndex) {
                    focusIndex?.let { index ->
                        focusRequesters.getOrNull(index)?.requestFocus()
                    }
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
            AddEditNoteContent(
                screenTitle = R.string.add_note,
                onBack = {},
                onSave = {},
                loading = false,
                userMessage = null,
                onShowUserMessage = {},
                content = "",
                focusIndex = null,
                onContentChanged = {},
                modifier = Modifier
            )
        }
    }
}

@Preview
@Composable
private fun EditNoteContentPreview() {
    UrgatTheme {
        Surface {
            AddEditNoteContent(
                screenTitle = R.string.edit_note,
                onBack = {},
                onSave = {},
                loading = false,
                userMessage = null,
                onShowUserMessage = {},
                content = "Note content",
                focusIndex = null,
                onContentChanged = {},
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
            AddEditNoteContent(
                screenTitle = R.string.add_note,
                onBack = {},
                onSave = {},
                loading = true,
                userMessage = null,
                onShowUserMessage = {},
                content = "",
                focusIndex = null,
                onContentChanged = {},
                modifier = Modifier
            )
        }
    }
}