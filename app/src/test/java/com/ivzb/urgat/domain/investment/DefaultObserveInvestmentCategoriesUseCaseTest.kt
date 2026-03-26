package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.util.Async
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
class DefaultObserveInvestmentCategoriesUseCaseTest {

    private lateinit var investmentsRepository: InvestmentsRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveInvestmentCategoriesUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        investmentsRepository = mockk()
        randomRepository = mockk()
        observeUseCase = DefaultObserveInvestmentCategoriesUseCase(investmentsRepository, randomRepository)

        every { randomRepository.uuid() } returns TEST_UUID
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)
        val investments = listOf(
            Investment("1", "SNP 500", BigDecimal("2"), BigDecimal("250"), BigDecimal("250"), "etfs", "USD", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Investment("2", "BRK.B", BigDecimal("2"), BigDecimal("330"), BigDecimal("330"), "etfs", "USD", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
            Investment("3", "GOOGL", BigDecimal("3.5"), BigDecimal("220.3"), BigDecimal("220.3"), "stocks", "USD", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),

            Investment("4", "SNP 500", BigDecimal("2"), BigDecimal("250"), BigDecimal("250"), "etfs", "EUR", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            Investment("5", "BRK.B", BigDecimal("2"), BigDecimal("330"), BigDecimal("330"), "etfs", "EUR", timestamp(2025, 12, 29), false, timestamp(2025, 12, 29)),
            Investment("6", "GOOGL", BigDecimal("3.5"), BigDecimal("220.3"), BigDecimal("220.3"), "stocks", "EUR", timestamp(2025, 12, 29), false, timestamp(2025, 12, 29)),

            Investment("7", "SNP 500", BigDecimal("2"), BigDecimal("250"), BigDecimal("250"), "etfs", "USD", timestamp(2024, 11, 25), false, timestamp(2024, 11, 25)),
            Investment("8", "BRK.B", BigDecimal("2"), BigDecimal("330"), BigDecimal("330"), "etfs", "USD", timestamp(2024, 11, 27), false, timestamp(2024, 11, 27)),
            Investment("9", "GOOGL", BigDecimal("3.5"), BigDecimal("220.3"), BigDecimal("220.3"), "stocks", "USD", timestamp(2024, 12, 9), false, timestamp(2024, 12, 9)),

            Investment("10", "SNP 500", BigDecimal("2"), BigDecimal("250"), BigDecimal("250"), "etfs", "EUR", timestamp(2024, 12, 17), false, timestamp(2024, 12, 17)),
            Investment("11", "BRK.B", BigDecimal("2"), BigDecimal("330"), BigDecimal("330"), "etfs", "EUR", timestamp(2024, 12, 29), false, timestamp(2024, 12, 29)),
            Investment("12", "GOOGL", BigDecimal("3.5"), BigDecimal("220.3"), BigDecimal("220.3"), "stocks", "EUR", timestamp(2024, 12, 29), false, timestamp(2024, 12, 29)),
        )
        val repositoryStream = flowOf(investments)

        every { investmentsRepository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            InvestmentCategory(
                id = TEST_UUID,
                name = "etfs",
                totals = listOf(
                    InvestmentTotal(currency = "EUR", costPrice = BigDecimal("2320"), marketPrice = BigDecimal("2320"), pnl = BigDecimal.ZERO),
                    InvestmentTotal(currency = "USD", costPrice = BigDecimal("2320"), marketPrice = BigDecimal("2320"), pnl = BigDecimal.ZERO),
                ),
                investments = listOf(investments[0], investments[1], investments[3], investments[4], investments[6], investments[7], investments[9], investments[10]),
            ),
            InvestmentCategory(
                id = TEST_UUID,
                name = "stocks",
                totals = listOf(
                    InvestmentTotal(currency = "EUR", costPrice = BigDecimal("1542.10"), marketPrice = BigDecimal("1542.10"), pnl = BigDecimal.ZERO),
                    InvestmentTotal(currency = "USD", costPrice = BigDecimal("1542.10"), marketPrice = BigDecimal("1542.10"), pnl = BigDecimal.ZERO),
                ),
                investments = listOf(investments[2], investments[5], investments[8], investments[11]),
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

        every { investmentsRepository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

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
