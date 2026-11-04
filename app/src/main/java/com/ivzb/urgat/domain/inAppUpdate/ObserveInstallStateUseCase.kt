package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ObserveInstallStateUseCase {

    operator fun invoke(): Flow<InAppInstallState>

}

class DefaultObserveInstallStateUseCase @Inject constructor(
    private val repository: InAppUpdateRepository
) : ObserveInstallStateUseCase {

    override operator fun invoke(): Flow<InAppInstallState> = repository.observeInstallState()

}
