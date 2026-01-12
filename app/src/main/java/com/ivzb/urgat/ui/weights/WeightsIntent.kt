package com.ivzb.urgat.ui.weights

sealed class WeightsIntent {
    data class SetSorting(val sortType: WeightsSortType): WeightsIntent()
}