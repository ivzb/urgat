package com.ivzb.urgat.ui.components

import android.os.Build
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.LibraryDestination
import com.ivzb.urgat.navigation.WellbeingDestination
import com.ivzb.urgat.navigation.UrgatDestination
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.parcelize.Parcelize

@Composable
fun topBarInsets() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
    WindowInsets()
} else {
    WindowInsets.statusBars
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    selectedDestination: UrgatDestination,
    onSearchQueryChanged: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    withActions: Boolean = true,
    actionsContent: (@Composable () -> Unit)? = null
) {
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    SearchableTopBar(
        isSearching = isSearching,
        onIsSearchingChanged = { isSearching = it },
        searchQuery = searchQuery,
        onSearchQueryChanged = {
            searchQuery = it
            onSearchQueryChanged(it)
        },
        titleResId = selectedDestination.label,
        onBack = onBack,
        withActions = withActions,
        actionsContent = actionsContent
    )
}

@Composable
fun TopAppBarDropdownMenu(
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            iconContent()
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            content { expanded = !expanded }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTopAppBar(
    @StringRes title: Int?,
    onBack: () -> Unit,

    withDelete: Boolean,
    onDelete: () -> Unit,
) {
    TopAppBar(
        title = {
            title?.let {
                Text(text = stringResource(it))
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
            }
        },
        actions = {
            if (withDelete) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
        },
        windowInsets = topBarInsets(),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Parcelize
data class FilterMenuItem<T: Parcelable>(
    val type: T? = null,
    val content: String? = null,
    val labelResId: Int? = null,
    val label: String = "",
    val size: Int,
): Parcelable {

    @Composable
    fun text(): String {
        val text = labelResId?.let { stringResource(it) } ?: label
        return "$text ($size)"
    }
}

@Composable
fun <T: Parcelable> FilterMenu(
    items: List<FilterMenuItem<T>>,
    onFilter: (FilterMenuItem<T>) -> Unit,
) {
    TopAppBarDropdownMenu(
        iconContent = {
            Icon(
                painterResource(id = R.drawable.ic_filter_list),
                stringResource(id = R.string.menu_filter)
            )
        }
    ) { closeMenu ->

        for (item in items) {
            DropdownMenuItem(
                onClick = {
                    onFilter(item);
                    closeMenu()
                },
                text = {
                    Text(text = item.text())
                }
            )
        }
    }
}

@Composable
fun MoreTodosMenu(
    onClearCompletedTodos: () -> Unit
) {
    TopAppBarDropdownMenu(
        iconContent = {
            Icon(Icons.Filled.MoreVert, stringResource(id = R.string.more))
        }
    ) { closeMenu ->
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.clear_completed)) },
            onClick = {
                onClearCompletedTodos()
                closeMenu()
            }
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentLibraryDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = LibraryDestination,
            onSearchQueryChanged = {},
            onBack = {},
            withActions = true,
            actionsContent = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(id = R.drawable.ic_filter_list),
                        stringResource(id = R.string.menu_filter)
                    )
                }
            }
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentLibraryDestinationPreviewWithoutBack() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = LibraryDestination,
            onSearchQueryChanged = {},
            withActions = true,
            actionsContent = {
                IconButton(onClick = {}) {
                    Icon(
                        painterResource(id = R.drawable.ic_filter_list),
                        stringResource(id = R.string.menu_filter)
                    )
                }
            }
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentLibraryDestinationWithoutActionsPreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = LibraryDestination,
            onSearchQueryChanged = {},
            withActions = false,
            onBack = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentLibraryDestinationWithoutActionsPreviewWithoutBack() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = LibraryDestination,
            withActions = false,
            onSearchQueryChanged = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentWellbeingDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = WellbeingDestination,
            onSearchQueryChanged = {},
            withActions = false,
            actionsContent = {}
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun TextTopAppBarPreview() {
    UrgatTheme {
        Surface {
            TextTopAppBar(
                title = R.string.add_todo,
                onBack = {},
                withDelete = false,
                onDelete = {},
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun TextTopAppBarPreviewWithDelete() {
    UrgatTheme {
        Surface {
            TextTopAppBar(
                title = R.string.add_todo,
                onBack = {},
                withDelete = true,
                onDelete = {},
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun MoreTodosMenuPreview() {
    UrgatTheme {
        MoreTodosMenu(
            onClearCompletedTodos = {}
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun FilterMenuPreview() {
    UrgatTheme {
        FilterMenu(
            items = listOf(
                FilterMenuItem(label = "All", size = 10),
                FilterMenuItem(label = "Active", size = 5),
                FilterMenuItem(label = "Completed", size = 2),
            ),
            onFilter = {}
        )
    }
}