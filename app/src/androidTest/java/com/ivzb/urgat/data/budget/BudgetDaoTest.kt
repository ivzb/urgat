package com.ivzb.urgat.data.budget

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
class BudgetDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var budgetDao: BudgetDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        budgetDao = db.budgetDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val budgets = SampleData.SAMPLE_BUDGETS.map { it.toEntity() }

        budgets.forEach { budget ->
            budgetDao.upsert(budget)
            assertEquals(budget, budgetDao.getById(budget.id))

            val updated = budget.copy(
                income = budget.income + BigDecimal.ONE,
                bankStart = budget.bankStart + BigDecimal.ONE,
                bankEnd = budget.bankEnd?.let { it + BigDecimal.ONE },
                currency = budget.currency + " updated",
                date = budget.date + 1,
            )

            budgetDao.upsert(updated)
            assertEquals(updated, budgetDao.getById(budget.id))

            budgetDao.deleteById(budget.id)
            assertNull(budgetDao.getById(budget.id))
        }

        budgets.forEach { budget ->
            budgetDao.upsert(budget)
        }

        // collect flow
        val actual = budgetDao.observeAll(-1, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(budgets.sortedWith(compareByDescending<BudgetEntity> { it.date }.thenBy { it.id }), actual)

        val actualLimit = budgetDao.observeAll(3, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(budgets.sortedWith(compareByDescending<BudgetEntity> { it.date }.thenBy { it.id }).take(3), actualLimit)
    }

}

