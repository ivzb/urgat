package com.ivzb.urgat.domain.saving

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
class DefaultObserveGroupedSavingsYearlyUseCaseTest {

    private lateinit var repository: SavingsRepository
    private lateinit var observeUseCase: ObserveGroupedSavingsYearlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveGroupedSavingsYearlyUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("fund")
        val limit = 10
        val sortTypeFlow = flowOf(SavingsSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

                Saving("4", "Emergency fund", BigDecimal("500"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("5", "Kids fund", BigDecimal("200"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("6", "Cash", BigDecimal("250"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                Saving("7", "Emergency fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("8", "Kids fund", BigDecimal("200"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("9", "Cash", BigDecimal("250"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            ),
        )

        every { repository.observe(limit, "fund") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "2026",
                total = "950".toBigDecimal(),
                items = listOf(
                    Saving("7", "Emergency fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("8", "Kids fund", BigDecimal("200"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("9", "Cash", BigDecimal("250"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                ),
            ),
            GroupedItems(
                group = "2025",
                total = "1900".toBigDecimal(),
                items = listOf(
                    Saving("4", "Emergency fund", BigDecimal("500"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("5", "Kids fund", BigDecimal("200"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                    Saving("6", "Cash", BigDecimal("250"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

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
        val limit = 10
        val sortTypeFlow = flowOf(SavingsSortType.BY_MIN)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

                Saving("4", "Emergency fund", BigDecimal("500"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("5", "Kids fund", BigDecimal("200"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("6", "Cash", BigDecimal("250"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                Saving("7", "Emergency fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("8", "Kids fund", BigDecimal("200"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("9", "Cash", BigDecimal("250"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            ),
        )

        every { repository.observe(limit, "fund") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "2026",
                total = "950".toBigDecimal(),
                items = listOf(
                    Saving("8", "Kids fund", BigDecimal("200"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("9", "Cash", BigDecimal("250"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("7", "Emergency fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                ),
            ),
            GroupedItems(
                group = "2025",
                total = "1900".toBigDecimal(),
                items = listOf(
                    Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("5", "Kids fund", BigDecimal("200"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                    Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("6", "Cash", BigDecimal("250"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                    Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("4", "Emergency fund", BigDecimal("500"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
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
        val limit = 10
        val sortTypeFlow = flowOf(SavingsSortType.BY_MAX)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

                Saving("4", "Emergency fund", BigDecimal("500"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("5", "Kids fund", BigDecimal("200"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
                Saving("6", "Cash", BigDecimal("250"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                Saving("7", "Emergency fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("8", "Kids fund", BigDecimal("200"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                Saving("9", "Cash", BigDecimal("250"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            ),
        )

        every { repository.observe(limit, "fund") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "2026",
                total = "950".toBigDecimal(),
                items = listOf(
                    Saving("7", "Emergency fund", BigDecimal("500"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("9", "Cash", BigDecimal("250"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                    Saving("8", "Kids fund", BigDecimal("200"), timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
                ),
            ),
            GroupedItems(
                group = "2025",
                total = "1900".toBigDecimal(),
                items = listOf(
                    Saving("1", "Emergency fund", BigDecimal("500"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("4", "Emergency fund", BigDecimal("500"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                    Saving("3", "Cash", BigDecimal("250"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("6", "Cash", BigDecimal("250"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

                    Saving("2", "Kids fund", BigDecimal("200"), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
                    Saving("5", "Kids fund", BigDecimal("200"), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
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
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
