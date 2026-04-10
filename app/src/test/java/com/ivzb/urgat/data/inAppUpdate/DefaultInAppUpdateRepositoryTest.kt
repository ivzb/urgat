package com.ivzb.urgat.data.inAppUpdate

import android.app.Activity
import com.google.android.gms.tasks.Tasks
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultInAppUpdateRepositoryTest {

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var repository: DefaultInAppUpdateRepository

    @Before
    fun setUp() {
        appUpdateManager = mockk(relaxed = true)
        repository = DefaultInAppUpdateRepository(appUpdateManager)
    }

    @Test
    fun `checkForUpdate returns NoUpdate when no update available`() = runTest {
        val appUpdateInfo = mockk<AppUpdateInfo> {
            every { installStatus() } returns InstallStatus.UNKNOWN
            every { updateAvailability() } returns UpdateAvailability.UPDATE_NOT_AVAILABLE
        }
        every { appUpdateManager.appUpdateInfo } returns Tasks.forResult(appUpdateInfo)

        val result = repository.checkForUpdate()

        assertEquals(InAppUpdateStatus.NoUpdate, result)
    }

    @Test
    fun `checkForUpdate returns Downloaded when install status is downloaded`() = runTest {
        val appUpdateInfo = mockk<AppUpdateInfo> {
            every { installStatus() } returns InstallStatus.DOWNLOADED
            every { updateAvailability() } returns UpdateAvailability.UPDATE_AVAILABLE
        }
        every { appUpdateManager.appUpdateInfo } returns Tasks.forResult(appUpdateInfo)

        val result = repository.checkForUpdate()

        assertEquals(InAppUpdateStatus.Downloaded, result)
    }

    @Test
    fun `checkForUpdate returns UpdateAvailable when flexible update allowed`() = runTest {
        val appUpdateInfo = mockk<AppUpdateInfo> {
            every { installStatus() } returns InstallStatus.UNKNOWN
            every { updateAvailability() } returns UpdateAvailability.UPDATE_AVAILABLE
            every { isUpdateTypeAllowed(any<Int>()) } returns true
        }
        every { appUpdateManager.appUpdateInfo } returns Tasks.forResult(appUpdateInfo)

        val result = repository.checkForUpdate()

        assertEquals(InAppUpdateStatus.UpdateAvailable, result)
    }

    @Test
    fun `checkForUpdate returns InProgress when developer triggered update in progress`() = runTest {
        val appUpdateInfo = mockk<AppUpdateInfo> {
            every { installStatus() } returns InstallStatus.UNKNOWN
            every { updateAvailability() } returns UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            every { isUpdateTypeAllowed(any<Int>()) } returns false
        }
        every { appUpdateManager.appUpdateInfo } returns Tasks.forResult(appUpdateInfo)

        val result = repository.checkForUpdate()

        assertEquals(InAppUpdateStatus.InProgress, result)
    }

    @Test
    fun `checkForUpdate returns NoUpdate on exception`() = runTest {
        every { appUpdateManager.appUpdateInfo } returns Tasks.forException(RuntimeException("Failed"))

        val result = repository.checkForUpdate()

        assertEquals(InAppUpdateStatus.NoUpdate, result)
    }

    @Test
    fun `observeInstallState emits Downloaded when listener fires`() = runTest {
        val listenerSlot = slot<InstallStateUpdatedListener>()
        every { appUpdateManager.registerListener(capture(listenerSlot)) } returns Unit

        val job = launch(UnconfinedTestDispatcher()) {
            val state = repository.observeInstallState().first()
            assertEquals(InAppInstallState.Downloaded, state)
        }

        val installState = mockk<InstallState> {
            every { installStatus() } returns InstallStatus.DOWNLOADED
        }
        listenerSlot.captured.onStateUpdate(installState)

        job.join()

        verify { appUpdateManager.registerListener(any()) }
    }

    @Test
    fun `observeInstallState emits Downloading with correct progress`() = runTest {
        val listenerSlot = slot<InstallStateUpdatedListener>()
        every { appUpdateManager.registerListener(capture(listenerSlot)) } returns Unit

        val job = launch(UnconfinedTestDispatcher()) {
            val state = repository.observeInstallState().first()
            assertEquals(
                InAppInstallState.Downloading(bytesDownloaded = 75, totalBytesToDownload = 150),
                state
            )
        }

        val installState = mockk<InstallState> {
            every { installStatus() } returns InstallStatus.DOWNLOADING
            every { bytesDownloaded() } returns 75L
            every { totalBytesToDownload() } returns 150L
        }
        listenerSlot.captured.onStateUpdate(installState)

        job.join()
    }

    @Test
    fun `observeInstallState emits Failed on failure`() = runTest {
        val listenerSlot = slot<InstallStateUpdatedListener>()
        every { appUpdateManager.registerListener(capture(listenerSlot)) } returns Unit

        val job = launch(UnconfinedTestDispatcher()) {
            val state = repository.observeInstallState().first()
            assertEquals(InAppInstallState.Failed, state)
        }

        val installState = mockk<InstallState> {
            every { installStatus() } returns InstallStatus.FAILED
        }
        listenerSlot.captured.onStateUpdate(installState)

        job.join()
    }

    @Test
    fun `observeInstallState emits Canceled on cancel`() = runTest {
        val listenerSlot = slot<InstallStateUpdatedListener>()
        every { appUpdateManager.registerListener(capture(listenerSlot)) } returns Unit

        val job = launch(UnconfinedTestDispatcher()) {
            val state = repository.observeInstallState().first()
            assertEquals(InAppInstallState.Canceled, state)
        }

        val installState = mockk<InstallState> {
            every { installStatus() } returns InstallStatus.CANCELED
        }
        listenerSlot.captured.onStateUpdate(installState)

        job.join()
    }

    @Test
    fun `completeUpdate calls appUpdateManager completeUpdate`() {
        repository.completeUpdate()

        verify(exactly = 1) { appUpdateManager.completeUpdate() }
    }

    @Test
    fun `startFlexibleUpdate does nothing when no cached update info`() {
        val activity = mockk<Activity>()

        repository.startFlexibleUpdate(activity)

        verify(exactly = 0) { appUpdateManager.startUpdateFlowForResult(any(), any<Activity>(), any(), any()) }
    }
}