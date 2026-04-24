package com.ivzb.urgat.data.trade

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.source.local.AppDatabase
import com.ivzb.urgat.data.source.sample.SampleData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class TradeDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var tradeDao: TradeDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        tradeDao = db.tradeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val trades = SampleData.SAMPLE_TRADES.map { it.toEntity() }

        trades.forEach { trade ->
            tradeDao.upsert(trade)
            assertEquals(trade, tradeDao.getById(trade.id))

            val updated = trade.copy(
                name = trade.name + " updated",
                shares = trade.shares + BigDecimal.ONE,
                marketPrice = trade.marketPrice + BigDecimal.ONE,
                fee = trade.fee + BigDecimal.ONE,
                currency = trade.currency + " updated",
                orderType = trade.orderType + " updated",
                date = trade.date + 1,
            )

            tradeDao.upsert(updated)
            assertEquals(updated, tradeDao.getById(trade.id))

            tradeDao.deleteById(trade.id)
            assertNull(tradeDao.getById(trade.id))
        }

        trades.forEach { trade ->
            tradeDao.upsert(trade)
        }

        // collect flow
        val actual = tradeDao.observeAll(-1, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(trades.sortedWith(compareByDescending<TradeEntity> { it.date }.thenBy { it.id }), actual)

        val actualLimit = tradeDao.observeAll(3, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(trades.sortedWith(compareByDescending<TradeEntity> { it.date }.thenBy { it.id }).take(3), actualLimit)
    }

}