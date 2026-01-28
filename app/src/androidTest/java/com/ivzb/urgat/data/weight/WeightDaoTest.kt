package com.ivzb.urgat.data.weight

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
class WeightDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var weightDao: WeightDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        weightDao = db.weightDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val weights = SampleData.SAMPLE_WEIGHTS.map { it.toEntity() }

        weights.forEach { weight ->
            weightDao.upsert(weight)
            assertEquals(weight, weightDao.getById(weight.id))

            val updated = weight.copy(
                value = weight.value + BigDecimal.ONE,
            )

            weightDao.upsert(updated)
            assertEquals(updated, weightDao.getById(weight.id))

            weightDao.deleteById(weight.id)
            assertNull(weightDao.getById(weight.id))
        }

        weights.forEach { weight ->
            weightDao.upsert(weight)
        }

        // collect flow
        val actual = weightDao.observeAll(-1, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(weights.sortedByDescending { it.createdAt }, actual)

        val actualLimit = weightDao.observeAll(3, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(weights.sortedByDescending { it.createdAt }.take(3), actualLimit)
    }

}