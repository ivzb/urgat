package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal

class FakeObserveFiReUseCase : ObserveFiReUseCase {

    private val flow = MutableStateFlow<Async<FiRe?>>(Async.Loading)

    var lastInvocation: Invocation? = null
        private set

    fun emit(value: Async<FiRe?>) {
        flow.value = value
    }

    override operator fun invoke(
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<FiRe?>> {
        lastInvocation = Invocation(
            withdrawalRateFlow = withdrawalRateFlow,
            returnOfInvestmentFlow = returnOfInvestmentFlow,
            taxRateFlow = taxRateFlow,
            dateRangeFlow = dateRangeFlow,
        )

        return flow
    }

    data class Invocation(
        val withdrawalRateFlow: Flow<BigDecimal?>,
        val returnOfInvestmentFlow: Flow<BigDecimal?>,
        val taxRateFlow: Flow<BigDecimal?>,
        val dateRangeFlow: Flow<LongRange>?,
    )

}