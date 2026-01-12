package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.weights.WeightsSortType

fun sortWeights(items: List<Weight>, sortType: WeightsSortType): List<Weight> {
    return when (sortType) {
        WeightsSortType.BY_DATE -> items.sortedByDescending { it.date }
        WeightsSortType.BY_MIN -> items.sortedBy { it.value }
        WeightsSortType.BY_MAX -> items.sortedByDescending { it.value }
    }
}