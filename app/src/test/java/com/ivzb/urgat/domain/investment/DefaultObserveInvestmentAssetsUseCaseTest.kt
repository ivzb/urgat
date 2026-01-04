package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesAllTimeUseCaseTest
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveInvestmentAssetsUseCaseTest {

    private lateinit var investmentsRepository: InvestmentsRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveInvestmentAssetsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        investmentsRepository = mockk()
        randomRepository = mockk()
        observeUseCase = DefaultObserveInvestmentAssetsUseCase(investmentsRepository, randomRepository)

        every { randomRepository.uuid() } returns TEST_UUID
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("snp")
        val limit = 10
        val limitFlow = flowOf(limit)
        val investments = listOf(
            Investment("1", "SNP 500", BigDecimal("2"), BigDecimal("250"), "etfs", "USD", 1L, false, 1L),
            Investment("2", "BRK.B", BigDecimal("2"), BigDecimal("330"), "etfs", "USD", 1L, false, 1L),
            Investment("3", "GOOGL", BigDecimal("3.5"), BigDecimal("220.3"), "stocks", "USD", 1L, false, 1L),

            Investment("4", "SNP 500", BigDecimal("2"), BigDecimal("250"), "etfs", "EUR", 1L, false, 1L),
            Investment("5", "BRK.B", BigDecimal("2"), BigDecimal("330"), "etfs", "EUR", 1L, false, 1L),
            Investment("6", "GOOGL", BigDecimal("3.5"), BigDecimal("220.3"), "stocks", "EUR", 1L, false, 1L),
        )

        val repositoryStream = flowOf(investments)

        every { investmentsRepository.observe(limit, "snp") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            InvestmentAsset(id = TEST_UUID, name = "BRK.B", category = "etfs", shares = BigDecimal("2"), marketValue = BigDecimal("660"), currency = "USD", investments = listOf(investments[1]), total = "660 USD",),
            InvestmentAsset(id = TEST_UUID, name = "BRK.B", category = "etfs", shares = BigDecimal("2"), marketValue = BigDecimal("660"), currency = "EUR", investments = listOf(investments[4]), total = "660 EUR",),

            InvestmentAsset(id = TEST_UUID, name = "GOOGL", category = "stocks", shares = BigDecimal("3.5"), marketValue = BigDecimal("771.05"), currency = "USD", investments = listOf(investments[2]), total = "771.05 USD",),
            InvestmentAsset(id = TEST_UUID, name = "GOOGL", category = "stocks", shares = BigDecimal("3.5"), marketValue = BigDecimal("771.05"), currency = "EUR", investments = listOf(investments[5]), total = "771.05 EUR",),

            InvestmentAsset(id = TEST_UUID, name = "SNP 500", category = "etfs", shares = BigDecimal("2"), marketValue = BigDecimal("500"), currency = "USD", investments = listOf(investments[0]), total = "500 USD",),
            InvestmentAsset(id = TEST_UUID, name = "SNP 500", category = "etfs", shares = BigDecimal("2"), marketValue = BigDecimal("500"), currency = "EUR", investments = listOf(investments[3]), total = "500 EUR",),
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

        every { investmentsRepository.observe(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    companion object {

        private const val TEST_UUID = "mock_id"

    }

}
