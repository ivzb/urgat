package com.ivzb.urgat.data.note

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

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var noteDao: NoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        noteDao = db.noteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val notes = SampleData.SAMPLE_NOTES.map { it.toEntity() }

        notes.forEach { note ->
            noteDao.upsert(note)
            assertEquals(note, noteDao.getById(note.id))

            val updated = note.copy(
                content = note.content + " updated",
            )

            noteDao.upsert(updated)
            assertEquals(updated, noteDao.getById(note.id))

            noteDao.deleteById(note.id)
            assertNull(noteDao.getById(note.id))
        }

        notes.forEach { note ->
            noteDao.upsert(note)
        }

        // collect flow
        val actual = noteDao.observeAll(-1, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(notes.sortedByDescending { it.createdAt }, actual)

        val actualLimit = noteDao.observeAll(3, NO_DATE_RANGE.first, NO_DATE_RANGE.last).first()
        assertEquals(notes.sortedByDescending { it.createdAt }.take(3), actualLimit)
    }

}