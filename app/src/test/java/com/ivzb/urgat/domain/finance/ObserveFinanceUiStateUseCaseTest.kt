package com.ivzb.urgat.domain.finance

import com.ivzb.urgat.util.Async
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveFinanceUiStateUseCaseTest : ObserveFinanceUiStateUseCaseTestBase() {

    @Test
    fun `initial state is loading`() = runTest {
        emitAllLoading()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when any use case returns error`() = runTest {
        emitAllError()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `emits success state with all data populated`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)

        assertEquals(fakeExpenses, uiState.expensesUiState.expenses)
        assertFalse(uiState.expensesUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.expensesUiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.expensesUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeInvestments, uiState.investmentsUiState.investments)
        assertFalse(uiState.investmentsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.investmentsUiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.investmentsUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeSavings, uiState.savingsUiState.savings)
        assertFalse(uiState.savingsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.savingsUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeBudgets, uiState.budgetsUiState.budgets)
        assertFalse(uiState.budgetsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.budgetsUiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.budgetsUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeReportBudget, uiState.reportBudget)

        assertEquals(fakeFiRe, uiState.fire)
    }

    @Test
    fun `emits success state with null report budget and fire`() = runTest {
        emitAllSuccess(reportBudget = null, fire = null)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertNull(uiState.reportBudget)
        assertNull(uiState.fire)
    }

    @Test
    fun `pages include report when reportBudget is not null`() = runTest {
        emitAllSuccess(reportBudget = fakeReportBudget)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNotNull(result.data.reportBudget)
    }

    @Test
    fun `pages exclude report when reportBudget is null`() = runTest {
        emitAllSuccess(reportBudget = null)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNull(result.data.reportBudget)
    }

    @Test
    fun `pages include fire when fire is not null`() = runTest {
        emitAllSuccess(fire = fakeFiRe)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNotNull(result.data.fire)
    }

    @Test
    fun `pages exclude fire when fire is null`() = runTest {
        emitAllSuccess(fire = null)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNull(result.data.fire)
    }
}
