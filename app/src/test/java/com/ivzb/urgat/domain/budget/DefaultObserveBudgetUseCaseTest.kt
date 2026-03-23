package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.collections.listOf
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveBudgetsUseCaseTest {

    private lateinit var repository: BudgetsRepository
    private lateinit var observeUseCase: ObserveBudgetsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveBudgetsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = BudgetsSortType.BY_DATE,
            expected = listOf(
                Budget("3", BigDecimal("6400"), BigDecimal("6400"), BigDecimal("2250"), "EUR", 3L, false, 3L),
                Budget("2", BigDecimal("6300"), BigDecimal("6300"), BigDecimal("1250"), "EUR", 2L, false, 2L),
                Budget("1", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("-1250"), "EUR", 1L, false, 1L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted by min`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = BudgetsSortType.BY_MIN,
            expected = listOf(
                Budget("1", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("-1250"), "EUR", 1L, false, 1L),
                Budget("2", BigDecimal("6300"), BigDecimal("6300"), BigDecimal("1250"), "EUR", 2L, false, 2L),
                Budget("3", BigDecimal("6400"), BigDecimal("6400"), BigDecimal("2250"), "EUR", 3L, false, 3L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted by max`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = BudgetsSortType.BY_MAX,
            expected = listOf(
                Budget("3", BigDecimal("6400"), BigDecimal("6400"), BigDecimal("2250"), "EUR", 3L, false, 3L),
                Budget("2", BigDecimal("6300"), BigDecimal("6300"), BigDecimal("1250"), "EUR", 2L, false, 2L),
                Budget("1", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("-1250"), "EUR", 1L, false, 1L),
            ),
        )
    }

    private fun `invoke should return Success with filtered items sorted by`(sortType: BudgetsSortType, expected: List<Budget>) = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("budget")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val sortTypeFlow = flowOf(sortType)
        val limit = 10
        val limitFlow = flowOf(limit)

        val repositoryStream = flowOf(
            listOf(
                Budget("1", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("-1250"), "EUR", 1L, false, 1L),
                Budget("2", BigDecimal("6300"), BigDecimal("6300"), BigDecimal("1250"), "EUR", 2L, false, 2L),
                Budget("3", BigDecimal("6400"), BigDecimal("6400"), BigDecimal("2250"), "EUR", 3L, false, 3L),
            )
        )

        every { repository.observe(limit, NO_DATE_RANGE, "budget") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
