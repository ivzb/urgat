package com.ivzb.urgat.data.todo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.todo.toEntity
import com.ivzb.urgat.data.source.local.AppDatabase
import com.ivzb.urgat.data.source.sample.SampleData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.internal.decodeStringToJsonTree
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var todoDao: TodoDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        todoDao = db.todoDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun sampleData() = runTest {
        val todos = SampleData.SAMPLE_TODOS.map { it.toEntity() }

        todos.forEach { todo ->
            todoDao.upsert(todo)
            assertEquals(todo, todoDao.getById(todo.id))

            val updated = todo.copy(
                title = todo.title + " updated",
                description = todo.description + " updated",
                isCompleted = !todo.isCompleted,
            )

            todoDao.upsert(updated)
            assertEquals(updated, todoDao.getById(todo.id))

            todoDao.deleteById(todo.id)
            assertNull(todoDao.getById(todo.id))
        }

        todos.forEach { todo ->
            todoDao.upsert(todo)
        }

        // collect flow
        val actual = todoDao.observeAll(-1).first()
        assertEquals(todos.sortedByDescending { it.createdAt }, actual)

        val actualLimit = todoDao.observeAll(3).first()
        assertEquals(todos.sortedByDescending { it.createdAt }.take(3), actualLimit)

        todos.forEach { todo ->
            todoDao.updateCompleted(todo.id, true)
        }

        todoDao.deleteCompleted()

        val actualAfterComplete = todoDao.observeAll(-1).first()
        assertTrue(actualAfterComplete.isEmpty())
    }

}