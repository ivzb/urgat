package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase.Companion.CLEAR_FILTER_ID
import com.ivzb.urgat.ui.components.FilterMenuItem
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
            FilterMenuItem(id = CLEAR_FILTER_ID, labelResId = R.string.filter_all, size = 5),
            FilterMenuItem(id = "etfs", label = "etfs", size = 2),
            FilterMenuItem(id = "stocks", label = "stocks", size = 2),
            FilterMenuItem(id = "gold", label = "gold", size = 1),
        )

        val repositoryStream = flowOf(
            listOf(
                Investment("1", "SNP 500", "USD", BigDecimal("350"), "etfs"),
                Investment("2", "BRK.B", "USD", BigDecimal("430"), "etfs"),
                Investment("3", "NVDA", "USD", BigDecimal("120"), "stocks"),
                Investment("4", "GOOG", "USD", BigDecimal("190"), "stocks"),
                Investment("5", "GOOG", "USD", BigDecimal("500"), "gold"),
            )
        )

        every { repository.getStream(NO_LIMIT) } returns repositoryStream

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
        every { repository.getStream(NO_LIMIT) } returns flow {
            throw RuntimeException("Test error")
        }

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
        assertEquals(R.string.error_investments_filtering, (first as Async.Error).errorMessage)
    }
}
