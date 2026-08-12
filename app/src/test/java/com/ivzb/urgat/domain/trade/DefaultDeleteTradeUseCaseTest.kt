package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteTradeUseCaseTest {

    private lateinit var tradeDao: TradeDao
    private lateinit var deleteUseCase: DeleteTradeUseCase

    @Before
    fun setUp() {
        tradeDao = mockk(relaxed = true)
        deleteUseCase = DefaultDeleteTradeUseCase(tradeDao)
    }

    @Test
    fun `invoke should call dao deleteById with correct parameters`() = runTest {
        val id = "123"

        deleteUseCase(id)

        coVerify(exactly = 1) { tradeDao.deleteById(id) }
    }

}

