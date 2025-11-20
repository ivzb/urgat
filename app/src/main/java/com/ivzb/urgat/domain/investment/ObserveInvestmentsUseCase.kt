package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.ui.investments.InvestmentsFilterMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<InvestmentsFilterMenuItem?>?,
        limit: Int
    ): Flow<Async<List<Investment>>>

}

class DefaultObserveInvestmentsUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveInvestmentsUseCase, BaseObserveUseCase<Investment, InvestmentsFilterMenuItem?>(repository) {

    override val errorMessage: Int = R.string.error_investments

    override fun filter(items: List<Investment>, filteringType: InvestmentsFilterMenuItem?): List<Investment> {
        if (filteringType == null || filteringType.type == InvestmentsFilterType.ALL_INVESTMENTS) {
            return items
        }

        return items.filter { it.category == filteringType.content }
    }

}