package com.ivzb.urgat.ui.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

private const val FAB_ENTER_DURATION_MILLIS = 200
private const val FAB_EXIT_DURATION_MILLIS = 150

fun fabEnterTransition(): EnterTransition =
    fadeIn(animationSpec = tween(FAB_ENTER_DURATION_MILLIS)) +
        scaleIn(initialScale = 0.8f) +
        slideInVertically(initialOffsetY = { it / 2 })

fun fabExitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(FAB_EXIT_DURATION_MILLIS)) +
        scaleOut(targetScale = 0.8f) +
        slideOutVertically(targetOffsetY = { it / 2 })
