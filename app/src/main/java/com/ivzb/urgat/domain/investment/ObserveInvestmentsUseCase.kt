package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.default.DefaultObserveUseCase
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Investment>>>

}

class DefaultObserveInvestmentsUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveInvestmentsUseCase {

    private val defaultObserveUseCase = DefaultObserveUseCase(
        repository = repository,
        filter = ::filter,
        errorMessageResId = R.string.error_investments,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Investment>>> = defaultObserveUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

    companion object {

        private fun filter(items: List<Investment>, filterType: String): List<Investment> {
            if (filterType == InvestmentsFilterType_All) {
                return items
            }

            return items.filter { it.category == filterType }
        }

    }

}