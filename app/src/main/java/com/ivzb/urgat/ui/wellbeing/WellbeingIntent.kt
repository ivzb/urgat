package com.ivzb.urgat.ui.wellbeing

sealed class WellbeingIntent {
    data class SearchQueryChanged(val query: String): WellbeingIntent()
    data class HeaderItemClick(val item: WellbeingHeaderItem): WellbeingIntent()
    object EventConsumed: WellbeingIntent()
}
