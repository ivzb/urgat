package com.ivzb.urgat.domain.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import javax.inject.Inject

interface CompleteUpdateUseCase {

    operator fun invoke()

}

class DefaultCompleteUpdateUseCase @Inject constructor(
    private val repository: InAppUpdateRepository
) : CompleteUpdateUseCase {

    override operator fun invoke() {
        repository.completeUpdate()
    }

}

