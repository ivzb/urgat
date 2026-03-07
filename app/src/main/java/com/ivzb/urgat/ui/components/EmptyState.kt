package com.ivzb.urgat.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.ivzb.urgat.R

@Composable
fun EmptyState(
    filteringUiInfo: FilteringUiInfo,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.content_padding),
                vertical = dimensionResource(R.dimen.content_padding),
            )
    ) {
        EmptyStateCard(
            iconRes = filteringUiInfo.emptyIconRes,
            label = filteringUiInfo.emptyLabel,
            message = filteringUiInfo.emptyMessage,
            buttonLabel = filteringUiInfo.emptyButtonLabel,
            onButtonClick = onButtonClick,
        )
    }
}