package com.ivzb.urgat.domain.investment

import androidx.annotation.ChecksSdkIntAtLeast
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase.Companion.CLEAR_FILTER
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limit: Int
    ): Flow<Async<List<Investment>>>

    companion object {

        const val CLEAR_FILTER = "All"
    }

}

class DefaultObserveInvestmentsUseCase @Inject constructor(
    investmentsRepository: InvestmentsRepository
): ObserveInvestmentsUseCase, BaseObserveUseCase<Investment, String>(investmentsRepository) {

    override val errorMessage: Int = R.string.error_investments

    override fun filter(items: List<Investment>, filteringType: String): List<Investment> {
        if (filteringType == CLEAR_FILTER) {
            return items
        }

        return items.filter { it.category == filteringType }
    }

}