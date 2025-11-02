package com.ivzb.urgat.ui.trackers

sealed class TrackersIntent {
    data class SearchQueryChanged(val query: String): TrackersIntent()
    data class HeaderItemClick(val item: TrackersHeaderItem): TrackersIntent()
    object EventConsumed: TrackersIntent()
}
