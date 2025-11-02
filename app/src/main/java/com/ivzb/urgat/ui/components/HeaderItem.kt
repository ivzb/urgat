package com.ivzb.urgat.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

data class HeaderItem<T>(
    val id: T,
    val icon: ImageVector,
    val label: Int
)