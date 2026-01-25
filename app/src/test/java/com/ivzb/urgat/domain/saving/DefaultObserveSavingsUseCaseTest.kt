package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.ui.savings.SavingsSortType
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
class ObserveSavingsUseCaseTest {

    private lateinit var repository: SavingsRepository
    private lateinit var observeUseCase: ObserveSavingsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveSavingsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = SavingsSortType.BY_DATE,
            expected = listOf(
                Saving("3", "Cash", BigDecimal("250"), 125, false, 1L),
                Saving("2", "Kids fund", BigDecimal("200"), 124, false, 1L),
                Saving("1", "Emergency fund", BigDecimal("500"), 123, false, 1L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted by min`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = SavingsSortType.BY_MIN,
            expected = listOf(
                Saving("2", "Kids fund", BigDecimal("200"), 124, false, 1L),
                Saving("3", "Cash", BigDecimal("250"), 125, false, 1L),
                Saving("1", "Emergency fund", BigDecimal("500"), 123, false, 1L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted by max`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = SavingsSortType.BY_MAX,
            expected = listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), 123, false, 1L),
                Saving("3", "Cash", BigDecimal("250"), 125, false, 1L),
                Saving("2", "Kids fund", BigDecimal("200"), 124, false, 1L),
            ),
        )
    }

    private fun `invoke should return Success with filtered items sorted by`(sortType: SavingsSortType, expected: List<Saving>) = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("fund")
        val sortTypeFlow = flowOf(sortType)
        val limit = 10
        val limitFlow = flowOf(limit)

        val repositoryStream = flowOf(
            listOf(
                Saving("1", "Emergency fund", BigDecimal("500"), 123, false, 1L),
                Saving("2", "Kids fund", BigDecimal("200"), 124, false, 1L),
                Saving("3", "Cash", BigDecimal("250"), 125, false, 1L),
            )
        )

        every { repository.observe(limit, "fund") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, sortTypeFlow, limitFlow).toList()

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
