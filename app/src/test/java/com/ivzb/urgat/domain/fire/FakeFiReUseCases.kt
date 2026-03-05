package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal

class FakeObserveFiReUseCase : ObserveFiReUseCase {

    private val flow = MutableStateFlow<Async<FiRe?>>(Async.Loading)

    fun emit(value: Async<FiRe?>) {
        flow.value = value
    }

    override operator fun invoke(
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<FiRe?>> {
        return flow
    }

}