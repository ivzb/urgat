package com.ivzb.urgat.ui.wellbeing

import com.ivzb.urgat.ui.weights.WeightsSortType

sealed class WellbeingIntent {
    data class FabActionClick(val itemId: WellbeingFabActionId): WellbeingIntent()
    data class SetWeightsSorting(val sortType: WeightsSortType): WellbeingIntent()
    object EventConsumed: WellbeingIntent()
}
