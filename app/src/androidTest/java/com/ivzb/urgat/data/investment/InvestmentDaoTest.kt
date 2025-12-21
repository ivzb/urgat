package com.ivzb.urgat.data.investment

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class InvestmentDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var investmentDao: InvestmentDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        investmentDao = db.investmentDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val investments = SampleData.SAMPLE_INVESTMENTS.map { it.toEntity() }

        investments.forEach { investment ->
            investmentDao.upsert(investment)
            assertEquals(investment, investmentDao.getById(investment.id))

            val updated = investment.copy(
                name = investment.name + " updated",
                shares = investment.shares + BigDecimal.ONE,
                cost = investment.cost + BigDecimal.ONE,
                category = investment.category + " updated",
                currency = investment.currency + " updated",
                date = investment.date + 1,
            )

            investmentDao.upsert(updated)
            assertEquals(updated, investmentDao.getById(investment.id))

            investmentDao.deleteById(investment.id)
            assertNull(investmentDao.getById(investment.id))
        }

        investments.forEach { investment ->
            investmentDao.upsert(investment)
        }

        // collect flow
        val actual = investmentDao.observeAll(-1).first()
        assertEquals(investments.sortedByDescending { it.date }, actual)

        val actualLimit = investmentDao.observeAll(3).first()
        assertEquals(investments.sortedByDescending { it.date }.take(3), actualLimit)
    }

}