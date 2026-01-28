package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.investments.InvestmentsSortType
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
class DefaultObserveGroupedInvestmentsAllTimeUseCaseTest {

    private lateinit var repository: InvestmentsRepository
    private lateinit var observeUseCase: ObserveGroupedInvestmentsAllTimeUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveGroupedInvestmentsAllTimeUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
        listOf(
                Investment("1", "Alphabet (Google)", BigDecimal("2200"), BigDecimal("323.44"), "Stocks", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                Investment("2", "Microsoft Corp.", BigDecimal("1800"), BigDecimal("485.5"), "Stocks", "USD", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
                Investment("3", "Amazon.com Inc.", BigDecimal("1700"), BigDecimal("229.16"), "Stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
                Investment("4", "Tesla Inc.", BigDecimal("1900"), BigDecimal("419.40"), "Stocks", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
                Investment("5", "NVIDIA Corp.", BigDecimal("1700"), BigDecimal("180.26"), "Stocks", "USD", timestamp(2026, 1, 9), false, timestamp(2026, 1, 9)),
                Investment("6", "Apple Inc.", BigDecimal("1900"), BigDecimal("276.97"), "Stocks", "USD", timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = GroupedItems(
            group = "",
            total = "3,604,585 USD",
            items = listOf(
                Investment("6", "Apple Inc.", BigDecimal("1900"), BigDecimal("276.97"), "Stocks", "USD", timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
                Investment("5", "NVIDIA Corp.", BigDecimal("1700"), BigDecimal("180.26"), "Stocks", "USD", timestamp(2026, 1, 9), false, timestamp(2026, 1, 9)),
                Investment("4", "Tesla Inc.", BigDecimal("1900"), BigDecimal("419.40"), "Stocks", "USD", 1765922400000, false, 1765922400000),
                Investment("3", "Amazon.com Inc.", BigDecimal("1700"), BigDecimal("229.16"), "Stocks", "USD", 1765231200000, false, 1765231200000),
                Investment("2", "Microsoft Corp.", BigDecimal("1800"), BigDecimal("485.5"), "Stocks", "USD", 1764194400000, false, 1764194400000),
                Investment("1", "Alphabet (Google)", BigDecimal("2200"), BigDecimal("323.44"), "Stocks", "USD",  1764021600000, false, 1764021600000),
            ),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by market value`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(InvestmentsSortType.BY_MARKET_VALUE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Investment("1", "Alphabet (Google)", BigDecimal("2200"), BigDecimal("323.44"), "Stocks", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                Investment("2", "Microsoft Corp.", BigDecimal("1800"), BigDecimal("485.5"), "Stocks", "USD", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
                Investment("3", "Amazon.com Inc.", BigDecimal("1700"), BigDecimal("229.16"), "Stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
                Investment("4", "Tesla Inc.", BigDecimal("1900"), BigDecimal("419.40"), "Stocks", "USD", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
                Investment("5", "NVIDIA Corp.", BigDecimal("1700"), BigDecimal("180.26"), "Stocks", "USD", timestamp(2026, 1, 9), false, timestamp(2026, 1, 9)),
                Investment("6", "Apple Inc.", BigDecimal("1900"), BigDecimal("276.97"), "Stocks", "USD", timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = GroupedItems(
            group = "",
            total = "3,604,585 USD",
            items = listOf(
                Investment("2", "Microsoft Corp.", BigDecimal("1800"), BigDecimal("485.5"), "Stocks", "USD", 1764194400000, false, 1764194400000),
                Investment("4", "Tesla Inc.", BigDecimal("1900"), BigDecimal("419.40"), "Stocks", "USD", 1765922400000, false, 1765922400000),
                Investment("1", "Alphabet (Google)", BigDecimal("2200"), BigDecimal("323.44"), "Stocks", "USD",  1764021600000, false, 1764021600000),
                Investment("6", "Apple Inc.", BigDecimal("1900"), BigDecimal("276.97"), "Stocks", "USD", timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
                Investment("3", "Amazon.com Inc.", BigDecimal("1700"), BigDecimal("229.16"), "Stocks", "USD", 1765231200000, false, 1765231200000),
                Investment("5", "NVIDIA Corp.", BigDecimal("1700"), BigDecimal("180.26"), "Stocks", "USD", timestamp(2026, 1, 9), false, timestamp(2026, 1, 9)),
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
