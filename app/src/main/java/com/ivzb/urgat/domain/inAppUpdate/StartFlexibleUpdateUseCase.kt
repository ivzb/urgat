package com.ivzb.urgat.domain.inAppUpdate

import android.app.Activity
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import javax.inject.Inject

interface StartFlexibleUpdateUseCase {

    operator fun invoke(activity: Activity)

}

class DefaultStartFlexibleUpdateUseCase @Inject constructor(
    private val repository: InAppUpdateRepository
) : StartFlexibleUpdateUseCase {

    override operator fun invoke(activity: Activity) {
        repository.startFlexibleUpdate(activity)
    }

}

