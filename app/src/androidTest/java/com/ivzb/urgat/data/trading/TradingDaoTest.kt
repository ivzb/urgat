package com.ivzb.urgat.data.trading

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
class TradingDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var tradingDao: TradingDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        tradingDao = db.tradingDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val tradings = SampleData.SAMPLE_TRADING.map { it.toEntity() }

        tradings.forEach { trading ->
            tradingDao.upsert(trading)
            assertEquals(trading, tradingDao.getById(trading.id))

            val updated = trading.copy(
                name = trading.name + " updated",
                shares = trading.shares + BigDecimal.ONE,
                marketPrice = trading.marketPrice + BigDecimal.ONE,
                fee = trading.fee + BigDecimal.ONE,
                currency = trading.currency + " updated",
                orderType = trading.orderType + " updated",
                date = trading.date + 1,
            )

            tradingDao.upsert(updated)
            assertEquals(updated, tradingDao.getById(trading.id))

            tradingDao.deleteById(trading.id)
            assertNull(tradingDao.getById(trading.id))
        }

        tradings.forEach { trading ->
            tradingDao.upsert(trading)
        }

        // collect flow
        val actual = tradingDao.observeAll(-1, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(tradings.sortedWith(compareByDescending<TradingEntity> { it.date }.thenBy { it.id }), actual)

        val actualLimit = tradingDao.observeAll(3, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(tradings.sortedWith(compareByDescending<TradingEntity> { it.date }.thenBy { it.id }).take(3), actualLimit)
    }

}