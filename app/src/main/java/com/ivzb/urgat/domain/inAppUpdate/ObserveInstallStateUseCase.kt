package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInstallStateUseCase {

    operator fun invoke(): Flow<InAppInstallState>

}

class DefaultObserveInstallStateUseCase @Inject constructor(
    private val repository: InAppUpdateRepository
) : ObserveInstallStateUseCase {

    override operator fun invoke(): Flow<InAppInstallState> {
        return repository.observeInstallState()
    }

}

