package com.ivzb.urgat.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Remembers [count] [LazyListState]s whose scroll positions survive the screen leaving
 * composition (e.g. navigating to another destination and back) and process recreation.
 */
@Composable
fun rememberLazyListStates(count: Int): List<LazyListState> = rememberSaveable(
    count,
    saver = listSaver(
        save = { states -> states.flatMap { listOf(it.firstVisibleItemIndex, it.firstVisibleItemScrollOffset) } },
        restore = { values -> values.chunked(2).map { (index, offset) -> LazyListState(index, offset) } },
    ),
) {
    List(count) { LazyListState() }
}

/**
 * Returns whether the lazy list is currently scrolling up.
 */
@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
