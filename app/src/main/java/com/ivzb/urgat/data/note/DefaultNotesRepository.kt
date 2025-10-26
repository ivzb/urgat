package com.ivzb.urgat.data.note

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultNotesRepository @Inject constructor(
    private val localDataSource: NoteDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : NotesRepository {

    override fun getStream(): Flow<List<Note>> {
        return localDataSource.observeAll().map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun getStream(searchQuery: String): Flow<List<Note>> {
        return localDataSource.observeAll(searchQuery).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun getStream(limit: Int): Flow<List<Note>> {
        return localDataSource.observeAll(limit).map { expenses ->
            withContext(dispatcher) {
                expenses.toExternal()
            }
        }
    }

    override fun getStream(limit: Int, searchQuery: String): Flow<List<Note>> {
        return localDataSource.observeAll(limit, searchQuery).map { expenses ->
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
        val Note = Note(
            id = id,
            content = content,
            isDeleted =  false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(Note.toEntity())
        return id
    }

    override suspend fun update(id: String, content: String) {
        val Note = get(id)?.copy(
            content = content
        ) ?: throw Exception("Todo (id $id) not found")

        localDataSource.upsert(Note.toEntity())
    }

    override suspend fun get(id: String): Note? {
        return localDataSource.getById(id)?.toExternal()
    }
}
