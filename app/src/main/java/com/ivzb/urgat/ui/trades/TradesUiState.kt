package com.ivzb.urgat.ui.trades

import com.ivzb.urgat.domain.trades.GroupedTrades
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class TradesUiState(
    val trades: List<GroupedTrades> = listOf(),

    override val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = trades.isEmpty(),

    override val pages: List<Page> = TRADES_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: TradesFilteringUiInfo = TradesFilteringUiInfo(),
): PagerUiState