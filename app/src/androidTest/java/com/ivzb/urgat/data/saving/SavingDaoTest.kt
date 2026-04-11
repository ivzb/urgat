package com.ivzb.urgat.data.saving

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
class SavingDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var savingDao: SavingDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        savingDao = db.savingDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val savings = SampleData.SAMPLE_SAVINGS.map { it.toEntity() }

        savings.forEach { saving ->
            savingDao.upsert(saving)
            assertEquals(saving, savingDao.getById(saving.id))

            val updated = saving.copy(
                name = saving.name + " updated",
                amount = saving.amount + BigDecimal.ONE,
                currency = saving.currency + " updated",
                date = saving.date + 1,
            )

            savingDao.upsert(updated)
            assertEquals(updated, savingDao.getById(saving.id))

            savingDao.deleteById(saving.id)
            assertNull(savingDao.getById(saving.id))
        }

        savings.forEach { saving ->
            savingDao.upsert(saving)
        }

        // collect flow
        val actual = savingDao.observeAll(-1, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(savings.sortedWith(compareByDescending<SavingEntity> { it.date }.thenBy { it.id }), actual)

        val actualLimit = savingDao.observeAll(3, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(savings.sortedWith(compareByDescending<SavingEntity> { it.date }.thenBy { it.id }).take(3), actualLimit)
    }

}

