package com.ivzb.urgat.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.finance.FinanceRoute
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.wellbeing.WellbeingRoute

abstract class UrgatRoute: NavKey {
    abstract val titleResId: Int
    open val withFAB: Boolean = false
}

abstract class UrgatMenuRoute: UrgatRoute() {
    override val withFAB: Boolean = false
}

val BOTTOM_NAVIGATION_SCREENS = listOf(
    BottomNavigationScreen(
        route = LibraryRoute,
        label = R.string.library,
        icon = Icons.Filled.Home,
    ),
    BottomNavigationScreen(
        route = FinanceRoute,
        label = R.string.finance,
        icon = Icons.Filled.AttachMoney,
    ),
    BottomNavigationScreen(
        route = WellbeingRoute,
        label = R.string.wellbeing,
        icon = Icons.Filled.Accessibility,
    ),
)

data class BottomNavigationScreen(
    val route: UrgatMenuRoute,
    @StringRes
    val label: Int,
    val icon: ImageVector,
)