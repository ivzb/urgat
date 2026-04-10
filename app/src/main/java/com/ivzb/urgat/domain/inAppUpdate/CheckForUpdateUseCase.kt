package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateStatus
import javax.inject.Inject

interface CheckForUpdateUseCase {

    suspend operator fun invoke(): InAppUpdateStatus

}

class DefaultCheckForUpdateUseCase @Inject constructor(
    private val repository: InAppUpdateRepository
) : CheckForUpdateUseCase {

    override suspend operator fun invoke(): InAppUpdateStatus {
        return repository.checkForUpdate()
    }

}

