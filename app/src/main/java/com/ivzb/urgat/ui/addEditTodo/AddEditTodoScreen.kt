package com.ivzb.urgat.ui.addEditTodo

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun AddEditTodoScreen(
    @StringRes screenTitle: Int,
    onTodoUpdate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditTodoViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditTodoContent(
        screenTitle = screenTitle,
        onBack = onBack,
        onSave = viewModel::saveTodo,
        loading = uiState.isLoading,
        title = uiState.title,
        description = uiState.description,
        onTitleChanged = viewModel::updateTitle,
        onDescriptionChanged = viewModel::updateDescription,
        modifier = modifier
    )

    // Check if the task is saved and call onTaskUpdate event
    LaunchedEffect(uiState.isTodoSaved) {
        if (uiState.isTodoSaved) {
            onTodoUpdate()
        }
    }

    // Check for user messages to display on the screen
    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)
        LaunchedEffect(snackbarHostState, viewModel, userMessage, snackbarText) {
            snackbarHostState.showSnackbar(snackbarText)
            viewModel.snackbarMessageShown()
        }
    }
}

@Composable
private fun AddEditTodoContent(
    screenTitle: Int,
    onBack: () -> Unit,
    onSave: () -> Unit,
    loading: Boolean,
    title: String,
    description: String,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ) {
        AddEditTodoTopAppBar(screenTitle, onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.content_padding))
                .verticalScroll(rememberScrollState())
        ) {
            if (loading) {
                Text("Loading...")
            } else {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSecondary
                )

                TextField(
                    value = title,
                    onValueChange = onTitleChanged,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.title_hint),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
//                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                TextField(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    placeholder = { Text(stringResource(id = R.string.description_hint)) },
//                    colors = textFieldColors,
                    modifier = Modifier
                        .height(150.dp)
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditTodoTopAppBar(@StringRes title: Int, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = stringResource(title))
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
            }
        },
        windowInsets = WindowInsets(0.dp),
        modifier = Modifier
            .fillMaxWidth()
    )
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
private fun AddEditTodoTopAppBarPreview() {
    UrgatTheme {
        Surface {
            AddEditTodoTopAppBar(R.string.add_todo) { }
        }
    }
}
