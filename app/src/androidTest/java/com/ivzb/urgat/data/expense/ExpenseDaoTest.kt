package com.ivzb.urgat.data.expense

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
class ExpenseDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var expenseDao: ExpenseDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        expenseDao = db.expenseDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val expenses = SampleData.SAMPLE_EXPENSES.map { it.toEntity() }

        expenses.forEach { expense ->
            expenseDao.upsert(expense)
            assertEquals(expense, expenseDao.getById(expense.id))

            val updated = expense.copy(
                name = expense.name + " updated",
                amount = expense.amount + BigDecimal.ONE,
                category = expense.category + " updated",
                date = expense.date + 1,
            )

            expenseDao.upsert(updated)
            assertEquals(updated, expenseDao.getById(expense.id))

            expenseDao.deleteById(expense.id)
            assertNull(expenseDao.getById(expense.id))
        }

        expenses.forEach { expense ->
            expenseDao.upsert(expense)
        }

        // collect flow
        val actual = expenseDao.observeAll(-1, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(expenses.sortedByDescending { it.date }, actual)

        val actualLimit = expenseDao.observeAll(3, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(expenses.sortedByDescending { it.date }.take(3), actualLimit)
    }

}