package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetWatchlistAssetUseCaseTest {

    private lateinit var repository: WatchlistAssetsRepository
    private lateinit var getUseCase: GetWatchlistAssetUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getUseCase = DefaultGetWatchlistAssetUseCase(repository)
    }

    @Test
    fun `invoke should return watchlist asset when repository returns asset`() = runTest {
        val id = "123"
        val expectedAsset = WatchlistAsset(id, "AAPL", false, 1L)
        coEvery { repository.get(id) } returns expectedAsset

        val result = getUseCase(id)

        assertEquals(expectedAsset, result)
        coVerify(exactly = 1) { repository.get(id) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val id = "999"
        coEvery { repository.get(id) } returns null

        val result = getUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { repository.get(id) }
    }
}

