package com.ivzb.urgat.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.navigation.TodoDestination
import com.ivzb.urgat.navigation.UrgatMenuDestination
import com.ivzb.urgat.navigation.bottomNavigationScreens
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun NavigationRailContent(
    currentDestination: UrgatMenuDestination,
    onItemClick: (UrgatMenuDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        bottomNavigationScreens.forEach { item ->
            NavigationRailItem(
                selected = currentDestination == item,
                onClick = {
                    onItemClick(item)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.label)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.label),
                        color = if (currentDestination == item) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview
@Composable
fun NavigationRailContentOverviewDestinationPreview() {
    UrgatTheme {
        NavigationRailContent(
            currentDestination = OverviewDestination,
            onItemClick = {}
        )
    }
}

@Preview
@Composable
fun NavigationRailContentTodoDestinationPreview() {
    UrgatTheme {
        NavigationRailContent(
            currentDestination = TodoDestination,
            onItemClick = {}
        )
    }
}
