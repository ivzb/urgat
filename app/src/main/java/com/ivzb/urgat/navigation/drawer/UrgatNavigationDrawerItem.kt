package com.ivzb.urgat.navigation.drawer

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.ivzb.urgat.navigation.UrgatRoute

data class UrgatNavigationDrawerItem(
    @StringRes
    val labelResId: Int,
    val iconVector: ImageVector,
    val route: UrgatRoute,
)