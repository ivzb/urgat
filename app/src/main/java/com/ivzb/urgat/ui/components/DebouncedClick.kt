package com.ivzb.urgat.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberDebouncedClick(
    debounceMillis: Long = 1_000L,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableStateOf(0L) }

    return {
        val now = System.currentTimeMillis()

        if (now - lastClickTime >= debounceMillis) {
            lastClickTime = now
            onClick()
        }
    }
}