package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
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
class ObserveInvestmentsUseCaseTest {

    private lateinit var repository: InvestmentsRepository
    private lateinit var observeUseCase: ObserveInvestmentsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveInvestmentsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("snp")
        val limit = 10
        val limitFlow = flowOf(limit)

        val repositoryStream = flowOf(
            listOf(
                Investment("1", "SNP 500", BigDecimal("350"), BigDecimal("250"), "etfs", "USD", 1L, false, 1L),
                Investment("2", "BRK.B", BigDecimal("430"), BigDecimal("330"), "etfs", "USD", 1L, false, 1L),
            )
        )

        every { repository.observe(limit, "snp") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(2, items.size)
        assertEquals("SNP 500", items[0].name)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
