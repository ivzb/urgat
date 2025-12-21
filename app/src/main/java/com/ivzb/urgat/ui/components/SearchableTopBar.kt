package com.ivzb.urgat.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableTopBar(
    isSearching: Boolean,
    onIsSearchingChanged: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    titleResId: Int,
    navigationIcon: (@Composable () -> Unit)? = null,
    withActions: Boolean = true,
    actionsContent: @Composable (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }

    Crossfade(
        modifier = Modifier.animateContentSize(),
        targetState = isSearching,
        label = stringResource(R.string.search)
    ) { target ->
        if (!target) {
            TopAppBar(
                title = {
                    Text(stringResource(titleResId))
                },
                navigationIcon = {
                    navigationIcon?.invoke()
                },
                actions = {
                    if (withActions || searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            onIsSearchingChanged(!isSearching)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    }

                    if (withActions) {
                        actionsContent?.invoke()
                    }
                },
                windowInsets = topBarInsets()
            )
        } else {
            var searchQueryTextFieldValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = searchQuery,
                        selection = TextRange(searchQuery.length)
                    )
                )
            }

            val keyboardController = LocalSoftwareKeyboardController.current

            TextField(
                value = searchQueryTextFieldValue,
                onValueChange = {
                    searchQueryTextFieldValue = it.copy(
                        selection = TextRange(it.text.length)
                    )

                    onSearchQueryChanged(it.text)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                placeholder = {
                    Text(stringResource(R.string.search))
                },
                leadingIcon = {
                    IconButton(onClick = {
                        onIsSearchingChanged(!isSearching)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = {
                            searchQueryTextFieldValue = searchQueryTextFieldValue.copy(
                                text = "",
                                selection = TextRange(0)
                            )
                            onSearchQueryChanged("")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.search_text_field_padding))
                    .focusRequester(focusRequester)
            )

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

@Preview
@DarkModePreview
@Composable
fun SearchableTopBarPreview() {
    UrgatTheme {
        SearchableTopBar(
            isSearching = false,
            onIsSearchingChanged = {},
            searchQuery = "",
            onSearchQueryChanged = {},
            titleResId = R.string.search,
            withActions = true,
            actionsContent = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(id = R.drawable.ic_filter_list),
                        stringResource(id = R.string.menu_filter)
                    )
                }
            },
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun SearchableTopBarPreviewWithBackButton() {
    UrgatTheme {
        SearchableTopBar(
            isSearching = false,
            onIsSearchingChanged = {},
            searchQuery = "",
            onSearchQueryChanged = {},
            titleResId = R.string.search,
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
                }
            },
            withActions = true,
            actionsContent = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(id = R.drawable.ic_filter_list),
                        stringResource(id = R.string.menu_filter)
                    )
                }
            },
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun SearchableTopBarPreviewWithMenuButton() {
    UrgatTheme {
        SearchableTopBar(
            isSearching = false,
            onIsSearchingChanged = {},
            searchQuery = "",
            onSearchQueryChanged = {},
            titleResId = R.string.search,
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            withActions = true,
            actionsContent = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(id = R.drawable.ic_filter_list),
                        stringResource(id = R.string.menu_filter)
                    )
                }
            },
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun SearchableTopBarWithoutActionsPreview() {
    UrgatTheme {
        SearchableTopBar(
            isSearching = false,
            onIsSearchingChanged = {},
            searchQuery = "",
            onSearchQueryChanged = {},
            withActions = false,
            titleResId = R.string.search,
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun SearchableTopBarWithoutActionsPreviewWithBackButton() {
    UrgatTheme {
        SearchableTopBar(
            isSearching = false,
            onIsSearchingChanged = {},
            searchQuery = "",
            onSearchQueryChanged = {},
            titleResId = R.string.search,
            withActions = false,
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun SearchableTopBarSearchingEmptyStatePreview() {
    UrgatTheme {
        SearchableTopBar(
            isSearching = true,
            onIsSearchingChanged = {},
            searchQuery = "",
            onSearchQueryChanged = {},
            titleResId = R.string.search,
            withActions = false,
            actionsContent = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun SearchableTopBarSearchingTextStatePreview() {
    UrgatTheme {
        SearchableTopBar(
            isSearching = true,
            onIsSearchingChanged = {},
            searchQuery = "John",
            onSearchQueryChanged = {},
            titleResId = R.string.search,
            withActions = false,
            actionsContent = {},
        )
    }
}
