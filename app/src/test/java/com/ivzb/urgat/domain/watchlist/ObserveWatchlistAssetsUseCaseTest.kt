package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class ObserveWatchlistAssetsUseCaseTest {

    private lateinit var repository: WatchlistAssetsRepository
    private lateinit var observeUseCase: ObserveWatchlistAssetsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        every { repository.isRefreshingFlow } returns MutableStateFlow(false)
        observeUseCase = DefaultObserveWatchlistAssetsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with items sorted by`(
            sortType = WatchlistAssetsSortType.BY_DATE,
            expected = listOf(
                WatchlistAsset(
                    id = "3",
                    name = "NVDA",
                    description = "NVIDIA Corporation (NVDA)",
                    marketPrice = BigDecimal("165.17"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("162.82"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 3L,
                ),
                WatchlistAsset(
                    id = "2",
                    name = "MSFT",
                    description = "Microsoft Corporation (MSFT)",
                    marketPrice = BigDecimal("358.96"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("361.15"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 2L,
                ),
                WatchlistAsset(
                    id = "1",
                    name = "AAPL",
                    description = "Apple Inc. (AAPL)",
                    marketPrice = BigDecimal("246.63"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("244.46"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 1L,
                ),
            ),
        )
    }

    @Test
    fun `invoke should return Success with items sorted by name`() = runTest(testDispatcher) {
        `invoke should return Success with items sorted by`(
            sortType = WatchlistAssetsSortType.BY_NAME,
            expected = listOf(
                WatchlistAsset(
                    id = "1",
                    name = "AAPL",
                    description = "Apple Inc. (AAPL)",
                    marketPrice = BigDecimal("246.63"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("244.46"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 1L,
                ),
                WatchlistAsset(
                    id = "2",
                    name = "MSFT",
                    description = "Microsoft Corporation (MSFT)",
                    marketPrice = BigDecimal("358.96"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("361.15"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 2L,
                ),
                WatchlistAsset(
                    id = "3",
                    name = "NVDA",
                    description = "NVIDIA Corporation (NVDA)",
                    marketPrice = BigDecimal("165.17"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("162.82"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 3L,
                ),
            ),
        )
    }

    private fun `invoke should return Success with items sorted by`(sortType: WatchlistAssetsSortType, expected: List<WatchlistAsset>) = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("A")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val sortTypeFlow = flowOf(sortType)
        val limit = 10
        val limitFlow = flowOf(limit)

        val repositoryStream = flowOf(
            listOf(
                WatchlistAsset(
                    id = "2",
                    name = "MSFT",
                    description = "Microsoft Corporation (MSFT)",
                    marketPrice = BigDecimal("358.96"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("361.15"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 2L,
                ),
                WatchlistAsset(
                    id = "1",
                    name = "AAPL",
                    description = "Apple Inc. (AAPL)",
                    marketPrice = BigDecimal("246.63"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("244.46"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 1L,
                ),
                WatchlistAsset(
                    id = "3",
                    name = "NVDA",
                    description = "NVIDIA Corporation (NVDA)",
                    marketPrice = BigDecimal("165.17"),
                    currency = "USD",
                    previousClosePrice = BigDecimal("162.82"),
                    pricePrecision = 2,
                    isDeleted = false,
                    createdAt = 3L,
                ),
            )
        )

        every { repository.observe(limit, NO_DATE_RANGE, "A") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

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

    @Test
    fun `isRefreshingFlow starts as false`() = runTest(testDispatcher) {
        assertEquals(false, observeUseCase.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow delegates from repository`() = runTest(testDispatcher) {
        val repositoryRefreshingFlow = MutableStateFlow(false)
        every { repository.isRefreshingFlow } returns repositoryRefreshingFlow

        val useCase = DefaultObserveWatchlistAssetsUseCase(repository)

        assertEquals(false, useCase.isRefreshingFlow.value)

        repositoryRefreshingFlow.value = true
        assertEquals(true, useCase.isRefreshingFlow.value)

        repositoryRefreshingFlow.value = false
        assertEquals(false, useCase.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow reflects repository state when true`() = runTest(testDispatcher) {
        val repositoryRefreshingFlow = MutableStateFlow(true)
        every { repository.isRefreshingFlow } returns repositoryRefreshingFlow

        val useCase = DefaultObserveWatchlistAssetsUseCase(repository)

        assertEquals(true, useCase.isRefreshingFlow.value)
    }
}

