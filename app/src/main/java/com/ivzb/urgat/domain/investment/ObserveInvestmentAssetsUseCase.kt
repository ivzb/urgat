package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.default.Group.groupInvestmentAssets
import com.ivzb.urgat.domain.default.ObserveGroupedUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentAssetsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<InvestmentAsset>>>

}

class DefaultObserveInvestmentAssetsUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveInvestmentAssetsUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterInvestments,
        group = ::groupInvestmentAssets,
        errorMessageResId = R.string.error_investments,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<InvestmentAsset>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}
