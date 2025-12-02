package com.ivzb.urgat.ui.wellbeing

sealed class WellbeingIntent {
    data class SearchQueryChanged(val query: String): WellbeingIntent()
    data class HeaderItemClick(val itemId: WellbeingHeaderItemId): WellbeingIntent()
    object EventConsumed: WellbeingIntent()
}
