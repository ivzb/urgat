package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultUpdateWatchlistAssetUseCaseTest {

    private lateinit var repository: WatchlistAssetsRepository
    private lateinit var updateUseCase: UpdateWatchlistAssetUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        updateUseCase = DefaultUpdateWatchlistAssetUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val name = "AAPL"

        updateUseCase(id, name)

        coVerify(exactly = 1) {
            repository.update(id, name)
        }
    }
}

