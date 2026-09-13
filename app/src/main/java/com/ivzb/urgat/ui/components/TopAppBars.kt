package com.ivzb.urgat.ui.components

import android.os.Build
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.wellbeing.WellbeingRoute
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Composable
fun topBarInsets() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
    WindowInsets()
} else {
    WindowInsets.statusBars
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    selectedRoute: UrgatRoute,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    withSearch: Boolean,
    navigationIcon: (@Composable () -> Unit)? = null,
    actionsContent: (@Composable () -> Unit)? = null,
    onSearchFocusChanged: ((Boolean) -> Unit)? = null,
) {
    var isSearching by rememberSaveable { mutableStateOf(false) }

    SearchableTopBar(
        isSearching = isSearching,
        onIsSearchingChanged = {
            isSearching = it
            onSearchFocusChanged?.invoke(it)
        },
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        titleResId = selectedRoute.titleResId,
        withSearch = withSearch,
        navigationIcon = navigationIcon,
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

    actionsContent: (@Composable () -> Unit)? = null,
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
            actionsContent?.invoke()
        },
        windowInsets = topBarInsets(),
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Parcelize
data class FilterMenuItem<T>(
    override val type: @RawValue T,
    override val labelResId: Int? = null,
    override val label: String = "",
    val size: Int,
): MenuItem<T>, Parcelable {

    @Composable
    override fun text(): String {
        val text = labelResId?.let { stringResource(it) } ?: label
        return "$text ($size)"
    }
}

@Parcelize
data class SortMenuItem<T>(
    override val type: @RawValue T,
    override val labelResId: Int? = null,
    override val label: String = "",
): MenuItem<T>, Parcelable {

    @Composable
    override fun text(): String {
        return labelResId?.let { stringResource(it) } ?: label
    }
}

interface MenuItem<T> {
    val type: @RawValue T
    val labelResId: Int?
    val label: String

    @Composable
    fun text(): String

}

@Composable
fun <T> FilterMenu(
    items: List<MenuItem<T>>,
    showBadge: Boolean,
    onFilter: (T) -> Unit,
    @DrawableRes
    iconResId: Int = R.drawable.ic_filter_list,
    @StringRes
    contentDescriptionResId: Int = R.string.menu_filter,
) {
    Menu(
        iconResId = iconResId,
        contentDescriptionResId = contentDescriptionResId,
        items = items,
        showBadge = showBadge,
        onFilter = onFilter,
    )
}

@Composable
fun <T> SortMenu(
    items: List<MenuItem<T>>,
    showBadge: Boolean,
    onFilter: (T) -> Unit,
) {
    Menu(
        iconResId = R.drawable.ic_sort_list,
        contentDescriptionResId = R.string.menu_sort,
        items = items,
        showBadge = showBadge,
        onFilter = onFilter,
    )
}

@Composable
private fun <T> Menu(
    @DrawableRes
    iconResId: Int,
    @StringRes
    contentDescriptionResId: Int,
    items: List<MenuItem<T>>,
    showBadge: Boolean,
    onFilter: (T) -> Unit,
) {
    TopAppBarDropdownMenu(
        iconContent = {
            BadgedBox(
                badge = {
                    if (showBadge) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.menu_badge_size)),
                        )
                    }
                }
            ) {
                Icon(
                    painterResource(id = iconResId),
                    stringResource(id = contentDescriptionResId)
                )
            }
        }
    ) { closeMenu ->

        for (item in items) {
            DropdownMenuItem(
                onClick = {
                    onFilter(item.type)
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
fun DeleteButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete)
        )
    }
}

@Composable
fun MoreMenu(
    items: List<Pair<Int, () -> Unit>>,
) {
    if (items.isEmpty()) {
        return
    }

    TopAppBarDropdownMenu(
        iconContent = {
            Icon(Icons.Filled.MoreVert, stringResource(id = R.string.more))
        }
    ) { closeMenu ->

        items.forEach { (textResId, completion) ->
            DropdownMenuItem(
                text = { Text(text = stringResource(id = textResId)) },
                onClick = {
                    completion()
                    closeMenu()
                }
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentLibraryRoutePreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedRoute = LibraryRoute,
            searchQuery = "",
            onSearchQueryChanged = {},
            withSearch = true,
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
fun TopAppBarContentLibraryRoutePreviewWithoutBack() {
    UrgatTheme {
        TopAppBarContent(
            selectedRoute = LibraryRoute,
            searchQuery = "",
            onSearchQueryChanged = {},
            withSearch = true,
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
fun TopAppBarContentLibraryRouteWithoutActionsPreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedRoute = LibraryRoute,
            searchQuery = "",
            onSearchQueryChanged = {},
            withSearch = false,
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentLibraryRouteWithoutActionsPreviewWithoutBack() {
    UrgatTheme {
        TopAppBarContent(
            selectedRoute = LibraryRoute,
            searchQuery = "",
            withSearch = false,
            onSearchQueryChanged = {},
        )
    }
}

@Preview
@DarkModePreview
@Composable
fun TopAppBarContentWellbeingRoutePreview() {
    UrgatTheme {
        TopAppBarContent(
            selectedRoute = WellbeingRoute,
            searchQuery = "",
            onSearchQueryChanged = {},
            withSearch = false,
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
                actionsContent = {
                    DeleteButton(onClick = {})
                }
            )
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun MoreMenuPreview() {
    UrgatTheme {
        MoreMenu(
            listOf(
                R.string.clear_completed to {},
            ),
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
                FilterMenuItem(type = "All", labelResId = R.string.filter_all, size = 10),
                FilterMenuItem(type = "Debit", labelResId = R.string.filter_debit, size = 2),
                FilterMenuItem(type = "Cost", labelResId = R.string.filter_cost, size = 8),
            ),
            showBadge = false,
            onFilter = {}
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun FilterMenuPreviewWithBadge() {
    UrgatTheme {
        FilterMenu(
            items = listOf(
                FilterMenuItem(type = "All", labelResId = R.string.filter_all, size = 10),
                FilterMenuItem(type = "Debit", labelResId = R.string.filter_debit, size = 2),
                FilterMenuItem(type = "Cost", labelResId = R.string.filter_cost, size = 8),
            ),
            showBadge = true,
            onFilter = {}
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun SortMenuPreview() {
    UrgatTheme {
        SortMenu(
            items = listOf(
                SortMenuItem(type = "By date", labelResId = R.string.filter_all),
                SortMenuItem(type = "By cost", labelResId = R.string.filter_cost),
            ),
            showBadge = false,
            onFilter = {}
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun SortMenuPreviewWithBadge() {
    UrgatTheme {
        SortMenu(
            items = listOf(
                SortMenuItem(type = "By date", labelResId = R.string.filter_all),
                SortMenuItem(type = "By cost", labelResId = R.string.filter_cost),
            ),
            showBadge = true,
            onFilter = {}
        )
    }
}
