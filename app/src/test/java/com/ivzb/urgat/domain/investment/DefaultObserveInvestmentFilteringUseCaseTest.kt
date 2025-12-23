package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
class ObserveInvestmentsFilteringUseCaseTest {

    private lateinit var repository: InvestmentsRepository
    private lateinit var observeUseCase: ObserveInvestmentsFilteringUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveInvestmentsFilteringUseCase(repository)
    }

    @Test
    fun `invoke should return Success with grouped items`() = runTest(testDispatcher) {
        val expected = listOf(
            FilterMenuItem(type = InvestmentsFilterType_All, labelResId = R.string.filter_all, size = 5),
            FilterMenuItem(type = "etfs", label = "etfs", size = 2),
            FilterMenuItem(type = "stocks", label = "stocks", size = 2),
            FilterMenuItem(type = "gold", label = "gold", size = 1),
        )

        val repositoryStream = flowOf(
            listOf(
                Investment("1", "SNP 500", BigDecimal("350"), BigDecimal("250"), "etfs", "USD", 1L, false, 1L),
                Investment("2", "BRK.B", BigDecimal("430"), BigDecimal("330"), "etfs", "USD", 1L, false, 1L),
                Investment("3", "NVDA", BigDecimal("120"), BigDecimal("250"), "stocks", "USD", 1L, false, 1L),
                Investment("4", "GOOG", BigDecimal("190"), BigDecimal("390"), "stocks", "USD", 1L, false, 1L),
                Investment("5", "GOOG", BigDecimal("500"), BigDecimal("400"), "gold", "USD", 1L, false, 1L),
            )
        )

        every { repository.observe(NO_ITEMS_LIMIT) } returns repositoryStream

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(expected, items)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        every { repository.observe(NO_ITEMS_LIMIT) } returns flow {
            throw RuntimeException("Test error")
        }

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
