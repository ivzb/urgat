package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase.Companion.CLEAR_FILTER_ID
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterMenuItem?>?,
        limit: Int
    ): Flow<Async<List<Investment>>>

    companion object {

        // todo: move to common place
        const val CLEAR_FILTER_ID = "all"
    }

}

class DefaultObserveInvestmentsUseCase @Inject constructor(
    investmentsRepository: InvestmentsRepository
): ObserveInvestmentsUseCase, BaseObserveUseCase<Investment, FilterMenuItem?>(investmentsRepository) {

    override val errorMessage: Int = R.string.error_investments

    override fun filter(items: List<Investment>, filteringType: FilterMenuItem?): List<Investment> {
        if (filteringType == null || filteringType.id == CLEAR_FILTER_ID) {
            return items
        }

        return items.filter { it.category == filteringType.id }
    }

}