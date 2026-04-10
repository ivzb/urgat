package com.ivzb.urgat.data.inAppUpdate

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface InAppUpdateRepository {

    suspend fun checkForUpdate(): InAppUpdateStatus

    fun observeInstallState(): Flow<InAppInstallState>

    fun startFlexibleUpdate(activity: Activity)

    fun completeUpdate()

}

@Singleton
class DefaultInAppUpdateRepository @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
) : InAppUpdateRepository {

    private var cachedUpdateInfo: AppUpdateInfo? = null

    override suspend fun checkForUpdate(): InAppUpdateStatus {
        return try {
            val info = appUpdateManager.appUpdateInfo.await()
            cachedUpdateInfo = info

            when {
                info.installStatus() == InstallStatus.DOWNLOADED -> {
                    InAppUpdateStatus.Downloaded
                }

                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && info.isFlexibleUpdateAllowed -> {
                    InAppUpdateStatus.UpdateAvailable
                }

                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && info.isImmediateUpdateAllowed -> {
                    InAppUpdateStatus.UpdateAvailable
                }

                info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    InAppUpdateStatus.InProgress
                }

                else -> {
                    InAppUpdateStatus.NoUpdate
                }
            }
        } catch (_: Exception) {
            InAppUpdateStatus.NoUpdate
        }
    }

    override fun observeInstallState(): Flow<InAppInstallState> = callbackFlow {
        val listener = InstallStateUpdatedListener { state ->
            val installState = when (state.installStatus()) {
                InstallStatus.DOWNLOADING -> InAppInstallState.Downloading(
                    bytesDownloaded = state.bytesDownloaded(),
                    totalBytesToDownload = state.totalBytesToDownload(),
                )
                InstallStatus.DOWNLOADED -> InAppInstallState.Downloaded
                InstallStatus.INSTALLING -> InAppInstallState.Installing
                InstallStatus.INSTALLED -> InAppInstallState.Installed
                InstallStatus.FAILED -> InAppInstallState.Failed
                InstallStatus.CANCELED -> InAppInstallState.Canceled
                else -> InAppInstallState.Idle
            }

            trySend(installState)
        }

        appUpdateManager.registerListener(listener)

        awaitClose {
            appUpdateManager.unregisterListener(listener)
        }
    }

    override fun startFlexibleUpdate(activity: Activity) {
        val info = cachedUpdateInfo ?: return

        appUpdateManager.startUpdateFlowForResult(
            info,
            activity,
            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
            IN_APP_UPDATE_REQUEST_CODE,
        )
    }

    override fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    companion object {

        const val IN_APP_UPDATE_REQUEST_CODE = 1001

    }

}

