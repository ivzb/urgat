package com.ivzb.urgat.ui.splash

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.sample.FakeSampleUseCase
import com.ivzb.urgat.ui.sample.SampleIntent
import com.ivzb.urgat.ui.sample.SampleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init has default ui state`() = runTest {
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isComplete)
    }

    @Test
    fun `initialize wait for 2 seconds and changes the ui state to complete`() = runTest {
        viewModel.onIntent(SplashIntent.Initialize)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertTrue(uiState.isComplete)
    }

    private fun createViewModel(): SplashViewModel {
        return SplashViewModel()
    }
}
