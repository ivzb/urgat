package com.ivzb.urgat.domain.finance

import com.ivzb.urgat.util.Async
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveFinanceUiStateUseCaseRefreshingTest : ObserveFinanceUiStateUseCaseTestBase() {

    @Test
    fun `investmentsUiState isRefreshing is false by default`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertFalse(result.data.investmentsUiState.isRefreshing)
    }

    @Test
    fun `investmentsUiState isRefreshing is true when watchlist is refreshing`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertTrue(result.data.investmentsUiState.isRefreshing)
    }

    @Test
    fun `investmentsUiState isRefreshing becomes false after refresh completes`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val resultRefreshing = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultRefreshing is Async.Success)
        assertTrue(resultRefreshing.data.investmentsUiState.isRefreshing)

        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(false)

        val resultDone = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultDone is Async.Success)
        assertFalse(resultDone.data.investmentsUiState.isRefreshing)
    }

    @Test
    fun `investmentsUiState isRefreshing does not affect loading state`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.investmentsUiState.isRefreshing)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `investmentsUiState isRefreshing does not affect other ui states`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.investmentsUiState.isRefreshing)
        assertFalse(uiState.expensesUiState.isLoading)
        assertFalse(uiState.investmentsUiState.isLoading)
        assertFalse(uiState.tradesUiState.isLoading)
        assertFalse(uiState.savingsUiState.isLoading)
        assertFalse(uiState.budgetsUiState.isLoading)
    }

    @Test
    fun `tradesUiState isRefreshing is false by default`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertFalse(result.data.tradesUiState.isRefreshing)
    }

    @Test
    fun `tradesUiState isRefreshing is true when trades is refreshing`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertTrue(result.data.tradesUiState.isRefreshing)
    }

    @Test
    fun `tradesUiState isRefreshing becomes false after refresh completes`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val resultRefreshing = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultRefreshing is Async.Success)
        assertTrue(resultRefreshing.data.tradesUiState.isRefreshing)

        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(false)

        val resultDone = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultDone is Async.Success)
        assertFalse(resultDone.data.tradesUiState.isRefreshing)
    }

    @Test
    fun `tradesUiState isRefreshing does not affect loading state`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.tradesUiState.isRefreshing)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `tradesUiState isRefreshing does not affect other ui states`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.tradesUiState.isRefreshing)
        assertFalse(uiState.expensesUiState.isLoading)
        assertFalse(uiState.investmentsUiState.isLoading)
        assertFalse(uiState.tradesUiState.isLoading)
        assertFalse(uiState.savingsUiState.isLoading)
        assertFalse(uiState.budgetsUiState.isLoading)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing is false by default`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertFalse(result.data.watchlistAssetsUiState.isRefreshing)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing is true when watchlist is refreshing`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertTrue(result.data.watchlistAssetsUiState.isRefreshing)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing becomes false after refresh completes`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val resultRefreshing = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultRefreshing is Async.Success)
        assertTrue(resultRefreshing.data.watchlistAssetsUiState.isRefreshing)

        fakeObserveWatchlistAssetsUseCase.emitRefreshing(false)

        val resultDone = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultDone is Async.Success)
        assertFalse(resultDone.data.watchlistAssetsUiState.isRefreshing)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing does not affect loading state`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.watchlistAssetsUiState.isRefreshing)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing does not affect other ui states`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.watchlistAssetsUiState.isRefreshing)
        assertFalse(uiState.expensesUiState.isLoading)
        assertFalse(uiState.investmentsUiState.isLoading)
        assertFalse(uiState.tradesUiState.isLoading)
        assertFalse(uiState.savingsUiState.isLoading)
        assertFalse(uiState.budgetsUiState.isLoading)
    }
}
