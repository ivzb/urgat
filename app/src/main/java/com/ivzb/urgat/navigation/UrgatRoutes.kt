package com.ivzb.urgat.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.car.CarRoute
import com.ivzb.urgat.ui.finance.FinanceRoute
import com.ivzb.urgat.ui.library.LibraryRoute
import com.ivzb.urgat.ui.wellbeing.WellbeingRoute

abstract class UrgatRoute: NavKey {

    abstract val titleResId: Int

}

abstract class UrgatRootRoute: UrgatRoute()

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
        route = CarRoute,
        label = R.string.car,
        icon = Icons.Filled.DirectionsCar,
    ),
    BottomNavigationScreen(
        route = WellbeingRoute,
        label = R.string.wellbeing,
        icon = Icons.Filled.Accessibility,
    ),
)

data class BottomNavigationScreen(
    val route: UrgatRootRoute,
    @StringRes
    val label: Int,
    val icon: ImageVector,
)