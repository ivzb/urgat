package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveWatchlistAssetsUseCaseTest {

    private lateinit var repository: WatchlistAssetsRepository
    private lateinit var observeUseCase: ObserveWatchlistAssetsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveWatchlistAssetsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with items sorted by`(
            sortType = WatchlistAssetsSortType.BY_DATE,
            expected = listOf(
                WatchlistAsset("3", "NVDA", false, 125),
                WatchlistAsset("2", "MSFT", false, 124),
                WatchlistAsset("1", "AAPL", false, 123),
            ),
        )
    }

    @Test
    fun `invoke should return Success with items sorted by name`() = runTest(testDispatcher) {
        `invoke should return Success with items sorted by`(
            sortType = WatchlistAssetsSortType.BY_NAME,
            expected = listOf(
                WatchlistAsset("1", "AAPL", false, 123),
                WatchlistAsset("2", "MSFT", false, 124),
                WatchlistAsset("3", "NVDA", false, 125),
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
                WatchlistAsset("1", "AAPL", false, 123),
                WatchlistAsset("2", "MSFT", false, 124),
                WatchlistAsset("3", "NVDA", false, 125),
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
}

