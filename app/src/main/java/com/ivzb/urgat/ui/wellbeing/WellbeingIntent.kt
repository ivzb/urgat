package com.ivzb.urgat.ui.wellbeing

sealed class WellbeingIntent {
    data class FabActionClick(val itemId: WellbeingFabActionId): WellbeingIntent()
    object EventConsumed: WellbeingIntent()
}
