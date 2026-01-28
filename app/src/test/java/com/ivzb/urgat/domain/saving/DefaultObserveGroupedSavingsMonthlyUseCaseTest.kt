package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.timestamp
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultObserveGroupedSavingsMonthlyUseCaseTest {

    private lateinit var repository: SavingsRepository
    private lateinit var observeUseCase: ObserveGroupedSavingsMonthlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveGroupedSavingsMonthlyUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("fund")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(SavingsSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

                Saving("4", "Emergency fund", BigDecimal("600"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("5", "Kids fund", BigDecimal("300"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("6", "Cash", BigDecimal("350"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                Saving("7", "Emergency fund", BigDecimal("700"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("8", "Kids fund", BigDecimal("400"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("9", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

                Saving("10", "Emergency fund", BigDecimal("800"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("11", "Kids fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("12", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "fund") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = "3300".toBigDecimal(),
                items = listOf(
                    Saving("7", "Emergency fund", BigDecimal("700"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("8", "Kids fund", BigDecimal("400"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("9", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

                    Saving("10", "Emergency fund", BigDecimal("800"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("11", "Kids fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("12", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = "1250".toBigDecimal(),
                items = listOf(
                    Saving("4", "Emergency fund", BigDecimal("600"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("5", "Kids fund", BigDecimal("300"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("6", "Cash", BigDecimal("350"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "950".toBigDecimal(),
                items = listOf(
                    Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                ),
            ),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by min`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("fund")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(SavingsSortType.BY_MIN)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

                Saving("4", "Emergency fund", BigDecimal("600"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("5", "Kids fund", BigDecimal("300"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("6", "Cash", BigDecimal("350"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                Saving("7", "Emergency fund", BigDecimal("700"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("8", "Kids fund", BigDecimal("400"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("9", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

                Saving("10", "Emergency fund", BigDecimal("800"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("11", "Kids fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("12", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "fund") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = "3300".toBigDecimal(),
                items = listOf(
                    Saving("8", "Kids fund", BigDecimal("400"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("9", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("12", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("11", "Kids fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("7", "Emergency fund", BigDecimal("700"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("10", "Emergency fund", BigDecimal("800"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = "1250".toBigDecimal(),
                items = listOf(
                    Saving("5", "Kids fund", BigDecimal("300"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("6", "Cash", BigDecimal("350"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("4", "Emergency fund", BigDecimal("600"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "950".toBigDecimal(),
                items = listOf(
                    Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                ),
            ),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by max`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("fund")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(SavingsSortType.BY_MAX)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

                Saving("4", "Emergency fund", BigDecimal("600"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("5", "Kids fund", BigDecimal("300"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("6", "Cash", BigDecimal("350"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                Saving("7", "Emergency fund", BigDecimal("700"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("8", "Kids fund", BigDecimal("400"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("9", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

                Saving("10", "Emergency fund", BigDecimal("800"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("11", "Kids fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("12", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "fund") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = "3300".toBigDecimal(),
                items = listOf(
                    Saving("10", "Emergency fund", BigDecimal("800"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("7", "Emergency fund", BigDecimal("700"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("11", "Kids fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("9", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("12", "Cash", BigDecimal("450"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("8", "Kids fund", BigDecimal("400"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = "1250".toBigDecimal(),
                items = listOf(
                    Saving("4", "Emergency fund", BigDecimal("600"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("6", "Cash", BigDecimal("350"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("5", "Kids fund", BigDecimal("300"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "950".toBigDecimal(),
                items = listOf(
                    Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                ),
            ),
        )

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
