package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.data.random.RandomRepository
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
class ObserveGroupedSavingCategoriesMonthlyUseCaseTest {

    private lateinit var savingsRepository: SavingsRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveGroupedSavingCategoriesMonthlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        savingsRepository = mockk()
        randomRepository = mockk()
        observeUseCase = DefaultObserveGroupedSavingCategoriesMonthlyUseCase(savingsRepository, randomRepository)

        every { randomRepository.uuid() } returns TEST_UUID
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val sortTypeFlow = flowOf(SavingsSortType.BY_DATE)
        val limit = 10
        val limitFlow = flowOf(limit)
        val savings = listOf(
            Saving("1", "Emergency fund", BigDecimal("500"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("2", "Kids fund", BigDecimal("200"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("3", "Cash", BigDecimal("250"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

            Saving("4", "Emergency fund", BigDecimal("600"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("5", "Kids fund", BigDecimal("300"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("6", "Cash", BigDecimal("350"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

            Saving("7", "Emergency fund", BigDecimal("700"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("8", "Kids fund", BigDecimal("400"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("9", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

            Saving("10", "Emergency fund", BigDecimal("800"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("11", "Kids fund", BigDecimal("500"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("12", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
        )
        val repositoryStream = flowOf(savings)

        every { savingsRepository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = "3300".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("1500"), savings = listOf(savings[6], savings[9])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("900"), savings = listOf(savings[7], savings[10])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("900"), savings = listOf(savings[8], savings[11])),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = "1250".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("600"), savings = listOf(savings[3])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("350"), savings = listOf(savings[5])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("300"), savings = listOf(savings[4])),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "950".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("500"), savings = listOf(savings[0])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("250"), savings = listOf(savings[2])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("200"), savings = listOf(savings[1])),
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
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val sortTypeFlow = flowOf(SavingsSortType.BY_MIN)
        val limit = 10
        val limitFlow = flowOf(limit)
        val savings = listOf(
            Saving("1", "Emergency fund", BigDecimal("500"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("2", "Kids fund", BigDecimal("200"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("3", "Cash", BigDecimal("250"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

            Saving("4", "Emergency fund", BigDecimal("600"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("5", "Kids fund", BigDecimal("300"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("6", "Cash", BigDecimal("350"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

            Saving("7", "Emergency fund", BigDecimal("700"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("8", "Kids fund", BigDecimal("400"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("9", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

            Saving("10", "Emergency fund", BigDecimal("800"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("11", "Kids fund", BigDecimal("500"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("12", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
        )
        val repositoryStream = flowOf(savings)

        every { savingsRepository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = "3300".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("1500"), savings = listOf(savings[6], savings[9])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("900"), savings = listOf(savings[7], savings[10])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("900"), savings = listOf(savings[8], savings[11])),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = "1250".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("600"), savings = listOf(savings[3])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("350"), savings = listOf(savings[5])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("300"), savings = listOf(savings[4])),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "950".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("500"), savings = listOf(savings[0])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("250"), savings = listOf(savings[2])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("200"), savings = listOf(savings[1])),
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
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val sortTypeFlow = flowOf(SavingsSortType.BY_MAX)
        val limit = 10
        val limitFlow = flowOf(limit)
        val savings = listOf(
            Saving("1", "Emergency fund", BigDecimal("500"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("2", "Kids fund", BigDecimal("200"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),
            Saving("3", "Cash", BigDecimal("250"), "EUR", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25),),

            Saving("4", "Emergency fund", BigDecimal("600"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("5", "Kids fund", BigDecimal("300"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),
            Saving("6", "Cash", BigDecimal("350"), "EUR", timestamp(2025, 12, 25), false, timestamp(2025, 12, 25),),

            Saving("7", "Emergency fund", BigDecimal("700"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("8", "Kids fund", BigDecimal("400"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("9", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),

            Saving("10", "Emergency fund", BigDecimal("800"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("11", "Kids fund", BigDecimal("500"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
            Saving("12", "Cash", BigDecimal("450"), "EUR", timestamp(2026, 1, 25), false, timestamp(2025, 1, 25),),
        )
        val repositoryStream = flowOf(savings)

        every { savingsRepository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "January 2026",
                total = "3300".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("1500"), savings = listOf(savings[9], savings[6])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("900"), savings = listOf(savings[10], savings[7])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("900"), savings = listOf(savings[8], savings[11])),
                ),
            ),
            GroupedItems(
                group = "December 2025",
                total = "1250".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("600"), savings = listOf(savings[3])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("350"), savings = listOf(savings[5])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("300"), savings = listOf(savings[4])),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = "950".toBigDecimal(),
                items = listOf(
                    SavingCategory(id = TEST_UUID, "Emergency fund", BigDecimal("500"), savings = listOf(savings[0])),
                    SavingCategory(id = TEST_UUID, "Cash", BigDecimal("250"), savings = listOf(savings[2])),
                    SavingCategory(id = TEST_UUID, "Kids fund", BigDecimal("200"), savings = listOf(savings[1])),
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

        every { savingsRepository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    companion object {

        private const val TEST_UUID = "mock_id"

    }

}
