package com.ivzb.urgat.ui.savings

import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.domain.saving.GroupedSavingCategories
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class SavingsUiState(
    val savings: List<GroupedSavings> = listOf(),
    val categoriesMonthly: List<GroupedSavingCategories> = listOf(),
    val categoriesYearly: List<GroupedSavingCategories> = listOf(),
    val categoriesAllTime: List<SavingCategory> = listOf(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = savings.isEmpty(),

    override val pages: List<Page> = SAVING_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: SavingsFilteringUiInfo = SavingsFilteringUiInfo()
): PagerUiState
