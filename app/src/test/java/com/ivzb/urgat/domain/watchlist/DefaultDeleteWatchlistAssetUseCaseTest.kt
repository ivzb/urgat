package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteWatchlistAssetUseCaseTest {

    private lateinit var repository: WatchlistAssetsRepository
    private lateinit var deleteUseCase: DeleteWatchlistAssetUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        deleteUseCase = DefaultDeleteWatchlistAssetUseCase(repository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteUseCase(id)

        coVerify(exactly = 1) {
            repository.delete(id)
        }
    }
}

