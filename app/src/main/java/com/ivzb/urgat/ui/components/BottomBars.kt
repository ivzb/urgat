package com.ivzb.urgat.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.navigation.OverviewDestination
import com.ivzb.urgat.navigation.UrgatMenuDestination
import com.ivzb.urgat.navigation.bottomNavigationScreens
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun BottomBarContent(
    currentDestination: UrgatMenuDestination,
    onItemClick: (UrgatMenuDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar (modifier = modifier) {
        bottomNavigationScreens.forEach { item ->
            NavigationBarItem(
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
                colors = NavigationBarItemDefaults.colors(
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
fun BottomBarContentOverviewDestinationPreview() {
    UrgatTheme {
        BottomBarContent(
            currentDestination = OverviewDestination,
            onItemClick = {}
        )
    }
}
