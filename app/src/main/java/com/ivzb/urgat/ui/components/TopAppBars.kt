package com.ivzb.urgat.ui.components

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.navigation.TrackersDestination
import com.ivzb.urgat.navigation.UrgatDestination
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    selectedDestination: UrgatDestination,
    onSearchQueryChanged: (String) -> Unit,
    onBack: (() -> Unit)? = null,
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
    @StringRes title: Int,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(title))
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
            }
        },
        windowInsets = WindowInsets(-1.dp),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Deprecated("Use FilterMenu instead")
@Composable
fun FilterTodosMenu(
    onFilterAllTodos: () -> Unit,
    onFilterActiveTodos: () -> Unit,
    onFilterCompletedTodos: () -> Unit
) {
    TopAppBarDropdownMenu(
        iconContent = {
            Icon(
                painterResource(id = R.drawable.ic_filter_list),
                stringResource(id = R.string.menu_filter)
            )
        }
    ) { closeMenu ->
        DropdownMenuItem(
            onClick = { onFilterAllTodos(); closeMenu() },
            text = { Text(text = stringResource(id = R.string.filter_all)) }
        )
        DropdownMenuItem(
            onClick = { onFilterActiveTodos(); closeMenu() },
            text = { Text(text = stringResource(id = R.string.nav_active)) }
        )
        DropdownMenuItem(
            onClick = { onFilterCompletedTodos(); closeMenu() },
            text = { Text(text = stringResource(id = R.string.nav_completed)) }
        )
    }
}

@Deprecated("Use FilterMenu instead")
@Composable
fun FilterExpensesMenu(
    onFilterAllExpenses: () -> Unit,
    onFilterDebitExpenses: () -> Unit,
    onFilterCostExpenses: () -> Unit
) {
    TopAppBarDropdownMenu(
        iconContent = {
            Icon(
                painterResource(id = R.drawable.ic_filter_list),
                stringResource(id = R.string.menu_filter)
            )
        }
    ) { closeMenu ->
        DropdownMenuItem(
            onClick = { onFilterAllExpenses(); closeMenu() },
            text = { Text(text = stringResource(id = R.string.filter_all)) }
        )
        DropdownMenuItem(
            onClick = { onFilterDebitExpenses(); closeMenu() },
            text = { Text(text = stringResource(id = R.string.nav_debit)) }
        )
        DropdownMenuItem(
            onClick = { onFilterCostExpenses(); closeMenu() },
            text = { Text(text = stringResource(id = R.string.nav_cost)) }
        )
    }
}

@Parcelize
data class FilterMenuItem(
    val id: String? = null,
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
fun FilterMenu(
    items: List<FilterMenuItem>,
    onFilter: (FilterMenuItem) -> Unit,
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
@Composable
fun TopAppBarContentOverviewDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = OverviewDestination,
            onSearchQueryChanged = {},
            onBack = {},
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
@Composable
fun TopAppBarContentOverviewDestinationPreviewWithoutBack() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = OverviewDestination,
            onSearchQueryChanged = {},
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
@Composable
fun TopAppBarContentOverviewDestinationWithoutActionsPreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = OverviewDestination,
            onSearchQueryChanged = {},
            onBack = {},
        )
    }
}

@Preview
@Composable
fun TopAppBarContentOverviewDestinationWithoutActionsPreviewWithoutBack() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = OverviewDestination,
            onSearchQueryChanged = {},
        )
    }
}

@Preview
@Composable
fun TopAppBarContentTrackersDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedDestination = TrackersDestination,
            onSearchQueryChanged = {},
            actionsContent = {}
        )
    }
}

@Preview
@Composable
private fun TextTopAppBarPreview() {
    UrgatTheme {
        Surface {
            TextTopAppBar(R.string.add_todo) { }
        }
    }
}

@Preview
@Composable
private fun FilterTodosMenuPreview() {
    UrgatTheme {
        FilterTodosMenu(
            onFilterAllTodos = {},
            onFilterActiveTodos = {},
            onFilterCompletedTodos = {}
        )
    }
}

@Preview
@Composable
private fun MoreTodosMenuPreview() {
    UrgatTheme {
        MoreTodosMenu(
            onClearCompletedTodos = {}
        )
    }
}

@Preview
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