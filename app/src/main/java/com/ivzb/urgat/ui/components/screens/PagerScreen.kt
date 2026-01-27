package com.ivzb.urgat.ui.components.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.navigation.UrgatRoute
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import kotlinx.coroutines.launch

typealias PagerContent = @Composable (
    page: Page,
    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) -> Unit

@Composable
fun PagerScreen(
    uiState: PagerUiState,

    route: UrgatRoute,
    onSearchQueryChanged: (String) -> Unit,
    onAddItem: () -> Unit,
    onBack: () -> Unit,

    withTopAppBarActions: Boolean = true,
    topAppBarActionsContent: (@Composable () -> Unit)? = null,

    content: PagerContent,

    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = uiState.initialPage) { uiState.pages.size }
    val listStates = remember { List(uiState.pages.size) { LazyListState() } }
    val currentListState = remember { mutableStateOf(listStates[0]) }

    ListDetailScreen(
        uiState = uiState,

        route = route,
        onSearchQueryChanged = onSearchQueryChanged,
        onAddItem = onAddItem,
        onBack = onBack,

        withTopAppBarActions = withTopAppBarActions,
        topAppBarActionsContent = topAppBarActionsContent,
        listState = currentListState.value,

        content = {
            PagerContent(
                pages = uiState.pages,
                pagerState = pagerState,
                listStates = listStates,
                onListStateChanged = { currentListState.value = it },
                content = content,
            )
        },

        modifier = modifier,
    )
}

@Composable
fun PagerScreen(
    uiState: PagerUiState,
    content: PagerContent,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = uiState.initialPage) { uiState.pages.size }
    val listStates = remember { List(uiState.pages.size) { LazyListState() } }
    val currentListState = remember { mutableStateOf(listStates[0]) }

    PagerContent(
        pages = uiState.pages,
        pagerState = pagerState,
        listStates = listStates,
        onListStateChanged = { currentListState.value = it },
        content = content,
        modifier = modifier,
    )
}

@Composable
fun PagerContent(
    pages: List<Page>,
    pagerState: PagerState,
    listStates: List<LazyListState>,
    onListStateChanged: (LazyListState) -> Unit,
    content: PagerContent,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val isHorizontalScrollEnabled by remember {
        derivedStateOf { pages[pagerState.currentPage].isHorizontalScrollEnabled }
    }

    Column (
        modifier = modifier,
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = {
                        Text(
                            text = stringResource(page.titleResId),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = isHorizontalScrollEnabled,
            modifier = Modifier
                .fillMaxSize(),
        ) { pageIndex ->

            content(
                pages[pageIndex],
                listStates[pageIndex],
                onListStateChanged,
            )
        }
    }
}