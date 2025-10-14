package com.ivzb.urgat.ui.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.navigation.TodoDestination
import com.ivzb.urgat.navigation.UrgatMenuDestination
import com.ivzb.urgat.ui.theme.UrgatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    currentDestination: UrgatMenuDestination,
    onSearchQueryChanged: (String) -> Unit,
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
        titleResId = currentDestination.label,
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

@Preview
@Composable
fun TopAppBarContentOverviewDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            currentDestination = OverviewDestination,
            onSearchQueryChanged = {}
        )
    }
}

@Preview
@Composable
fun TopAppBarContentTodoDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            currentDestination = TodoDestination,
            onSearchQueryChanged = {}
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
