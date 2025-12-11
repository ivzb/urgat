package com.ivzb.urgat.ui.addEditNote

import androidx.annotation.VisibleForTesting
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.DarkModeLandscapePreview
import com.ivzb.urgat.ui.components.DarkModePreview
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.components.rememberDebouncedClick
import com.ivzb.urgat.ui.components.responsiveButtonWidth
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.util.transparentTextFieldColors
import kotlinx.coroutines.launch

enum class FieldId {
    Content,
}

@Composable
fun AddEditNoteScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditNoteScreen(
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
fun AddEditNoteScreen(
    uiState: AddEditNoteUiState,
    onIntent: (AddEditNoteIntent) -> Unit,
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
                withDelete = uiState.withDelete,
                onDelete = { onIntent(AddEditNoteIntent.DeleteNote) },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                LoadingState(
                    label = R.string.loading,
                    modifier = Modifier.weight(1f)
                )
            } else {
                var contentTextFieldValue by remember { mutableStateOf(TextFieldValue(uiState.content)) }

                TextField(
                    value = contentTextFieldValue,
                    onValueChange = {
                        contentTextFieldValue = it
                        onIntent(AddEditNoteIntent.UpdateContent(it.text))
                    },
                    placeholder = { Text(text = stringResource(id = R.string.note_hint)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default // allow multiline
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
                    onClick = rememberDebouncedClick {
                        onIntent(AddEditNoteIntent.SaveNote)
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
                    contentTextFieldValue = TextFieldValue(
                        text = uiState.content,
                        selection = TextRange(uiState.content.length)
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
                onIntent(AddEditNoteIntent.UserMessageShown)
            }
        }
    }
}

@Preview
@DarkModePreview
@LandscapePreview
@DarkModeLandscapePreview
@Composable
private fun AddContentPreview() {
    UrgatTheme {
        Surface {
            AddEditNoteScreen(
                uiState = AddEditNoteUiState(
                    screenTitleResId = R.string.add_note,
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}

@Preview
@DarkModePreview
@LandscapePreview
@DarkModeLandscapePreview
@Composable
private fun EditNoteContentPreview() {
    UrgatTheme {
        Surface {
            AddEditNoteScreen(
                uiState = AddEditNoteUiState(
                    screenTitleResId = R.string.edit_note,
                    content = "Note content",
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}

@Preview
@DarkModePreview
@LandscapePreview
@DarkModeLandscapePreview
@Composable
private fun LoadingContentPreview() {
    UrgatTheme {
        Surface {
            AddEditNoteScreen(
                uiState = AddEditNoteUiState(
                    screenTitleResId = R.string.edit_note,
                    isLoading = true
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}