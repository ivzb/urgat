package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import com.ivzb.urgat.di.DefaultDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

interface ObserveInstallStateUseCase {

    operator fun invoke(): Flow<InAppInstallState>

}

class DefaultObserveInstallStateUseCase @Inject constructor(
    private val repository: InAppUpdateRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveInstallStateUseCase {

    override operator fun invoke(): Flow<InAppInstallState> = repository.observeInstallState()
        .flowOn(dispatcher)

}
