package com.ivzb.urgat.ui.trackers

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.TrackersDestination
import com.ivzb.urgat.ui.components.Header
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun TrackersScreen(
    onTrackWeight: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: TrackersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrackersScreenScaffold(
        headerItems = uiState.headerItems,
        onHeaderItemClick = { item -> viewModel.onIntent(TrackersIntent.HeaderItemClick(item)) },
        onSearchQueryChanged = {
            TODO("search")
        },
        navController = navController,
        modifier = modifier
    )

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let { event ->
            when (event) {
                is TrackersUiEvent.TrackWeight -> onTrackWeight()
            }

            viewModel.onIntent(TrackersIntent.EventConsumed)
        }
    }
}

@Composable
private fun TrackersScreenScaffold(
    headerItems: List<TrackerHeaderItem>,
    onHeaderItemClick: (TrackerHeaderItem) -> Unit,

    onSearchQueryChanged: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    MenuScaffold(
        selectedDestination = TrackersDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        withTopAppBarActions = false,
        navController = navController,
        modifier = modifier
    ) { listState ->

        val screenName = stringResource(R.string.overview)
        val configuration = LocalConfiguration.current
        val orientation = configuration.orientation

        Row (
            modifier = Modifier.fillMaxSize()
        ) {

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Header(
                    items = headerItems,
                    onItemClick = onHeaderItemClick,
                    orientation
                )
            }

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .semantics { contentDescription = screenName },
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(R.dimen.horizontal_margin),
                    vertical = dimensionResource(R.dimen.vertical_margin)
                ),
                state = listState
            ) {
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    item {
                        Header(
                            items = headerItems,
                            onItemClick = onHeaderItemClick,
                            orientation
                        )
                    }
                }
            }
        }

    }
}

@Composable
@Preview
@LandscapePreview
private fun TrackersScreenScaffoldPreview() {
    UrgatTheme {
        TrackersScreenScaffold(
            onSearchQueryChanged = {},
            navController = rememberNavController(),
            headerItems = listOf(
                HeaderItem(
                    TrackersHeaderItemId.Weight,
                    Icons.Filled.Timeline,
                    R.string.track_weight
                ),
            ),
            onHeaderItemClick = {},
            modifier = Modifier
        )
    }
}
