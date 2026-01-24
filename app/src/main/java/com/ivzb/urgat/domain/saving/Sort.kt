package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.ui.savings.SavingsSortType

fun sortSavings(items: List<Saving>, sortType: SavingsSortType): List<Saving> {
    return when (sortType) {
        SavingsSortType.BY_DATE -> items.sortedByDescending { it.date }
        SavingsSortType.BY_MIN -> items.sortedBy { it.amount }
        SavingsSortType.BY_MAX -> items.sortedByDescending { it.amount }
    }
}