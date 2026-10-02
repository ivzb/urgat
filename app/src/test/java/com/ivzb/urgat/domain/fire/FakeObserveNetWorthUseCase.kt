package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.NetWorth
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeObserveNetWorthUseCase : ObserveNetWorthUseCase {

    private val flow = MutableStateFlow<Async<NetWorth?>>(Async.Loading)

    var invocations = 0
        private set

    fun emit(value: Async<NetWorth?>) {
        flow.value = value
    }

    override fun invoke(): Flow<Async<NetWorth?>> {
        invocations++
        return flow
    }

}

