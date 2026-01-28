package com.ivzb.urgat.data.note

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultNotesRepository @Inject constructor(
    private val localDataSource: NoteDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : NotesRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Note>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Note>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override suspend fun create(content: String): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val note = Note(
            id = id,
            content = content,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(note.toEntity())
        return id
    }

    override suspend fun update(id: String, content: String) {
        val note = get(id)?.copy(
            content = content
        )

        if (note == null) {
            Assert.fail("Note (id $id) not found while update")
            return
        }

        localDataSource.upsert(note.toEntity())
    }

    override suspend fun get(id: String): Note? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }
}
