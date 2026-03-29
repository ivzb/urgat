package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateWatchlistAssetUseCaseTest {

    private lateinit var repository: WatchlistAssetsRepository
    private lateinit var createUseCase: CreateWatchlistAssetUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateWatchlistAssetUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "AAPL"

        createUseCase(name)

        coVerify(exactly = 1) {
            repository.create(name)
        }
    }
}

