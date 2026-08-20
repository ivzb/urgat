package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.data.fire.NetWorth
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Observes the accumulated stash (net worth) out of the FI/RE data.
 *
 * It is a thin projection over [ObserveFiReUseCase] so consumers which only care about the
 * net worth - like the home screen app widget - don't have to know about the FI/RE settings.
 *
 * The stash is always all time. It is the sum of every investment, trade and saving ever made, so
 * unlike the rest of the FI/RE data it is not affected by a date range.
 */
interface ObserveNetWorthUseCase {

    operator fun invoke(): Flow<Async<NetWorth?>>

}

class DefaultObserveNetWorthUseCase @Inject constructor(
    private val observeFiReUseCase: ObserveFiReUseCase,
    private val fireRepository: FireRepository,
): ObserveNetWorthUseCase {

    override fun invoke(): Flow<Async<NetWorth?>> {
        return observeFiReUseCase(
            withdrawalRateFlow = fireRepository.withdrawalRateFlow,
            returnOfInvestmentFlow = fireRepository.returnOfInvestmentFlow,
            taxRateFlow = fireRepository.taxRateFlow,
            // no date range, so budget and expenses of every month ever are taken into account
            dateRangeFlow = null,
        )
        .map { async ->
            when (async) {
                is Async.Success -> Async.Success(
                    async.data?.let { fire ->
                        NetWorth(
                            amount = fire.accumulatedStash,
                            currency = fire.currency,
                            breakdown = fire.stashBreakdown,
                        )
                    }
                )

                is Async.Error -> Async.Error
                is Async.Loading -> Async.Loading
            }
        }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
    }

}

