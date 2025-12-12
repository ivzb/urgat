package com.ivzb.urgat.ui.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class FabAction(
    val icon: ImageVector,
    @StringRes
    val labelResId: Int,
    val onClick: () -> Unit
)