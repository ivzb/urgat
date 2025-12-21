package com.ivzb.urgat.util

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.paddingIf(
    condition: Boolean,
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
): Modifier = if (condition) padding(horizontal = horizontal, vertical = vertical) else this

fun Modifier.paddingIf(
    condition: Boolean,
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
): Modifier = if (condition) padding(start = start, top = top, end = end, bottom = bottom) else this
