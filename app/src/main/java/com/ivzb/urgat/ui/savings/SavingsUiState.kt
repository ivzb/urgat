package com.ivzb.urgat.ui.savings

import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.domain.saving.GroupedSavingCategories
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class SavingsUiState(
    val savings: List<GroupedSavings> = emptyList(),
    val categoriesMonthly: List<GroupedSavingCategories> = emptyList(),
    val categoriesYearly: List<GroupedSavingCategories> = emptyList(),
    val categoriesAllTime: List<SavingCategory> = emptyList(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = savings.isEmpty(),

    override val pages: List<Page> = SAVING_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: SavingsFilteringUiInfo = SavingsFilteringUiInfo()
): PagerUiState
