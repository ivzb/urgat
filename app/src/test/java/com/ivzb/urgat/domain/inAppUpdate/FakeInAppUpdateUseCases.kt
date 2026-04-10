package com.ivzb.urgat.domain.inAppUpdate

import android.app.Activity
import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCheckForUpdateUseCase : CheckForUpdateUseCase {

    var result: InAppUpdateStatus = InAppUpdateStatus.NoUpdate

    override suspend fun invoke(): InAppUpdateStatus {
        return result
    }
}

class FakeObserveInstallStateUseCase : ObserveInstallStateUseCase {

    val flow = MutableSharedFlow<InAppInstallState>()

    override fun invoke(): Flow<InAppInstallState> {
        return flow
    }
}

class FakeStartFlexibleUpdateUseCase : StartFlexibleUpdateUseCase {

    var startedWithActivity: Activity? = null

    override fun invoke(activity: Activity) {
        startedWithActivity = activity
    }
}

class FakeCompleteUpdateUseCase : CompleteUpdateUseCase {

    var completed: Boolean = false

    override fun invoke() {
        completed = true
    }
}

