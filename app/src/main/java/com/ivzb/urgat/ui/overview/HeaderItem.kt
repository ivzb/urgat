package com.ivzb.urgat.ui.overview

import androidx.compose.ui.graphics.vector.ImageVector

data class HeaderItem(
    val id: HeaderItemId,
    val icon: ImageVector,
    val label: Int
)

enum class HeaderItemId {
    Todo,
    Expense,
    Note
}