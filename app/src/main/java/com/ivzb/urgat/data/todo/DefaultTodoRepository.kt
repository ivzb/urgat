package com.ivzb.urgat.data.todo

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTodoRepository @Inject constructor(
    private val localDataSource: TodoDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : TodoRepository {

    override suspend fun create(title: String, description: String): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val todo = Todo(
            id = id,
            title = title,
            description = description,
            isCompleted = false,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(todo.toEntity())
        return id
    }

    override suspend fun update(id: String, title: String, description: String) {
        val todo = get(id)?.copy(
            title = title,
            description = description
        )

        if (todo == null) {
            Assert.fail("Todo (id $id) not found while update")
            return
        }

        localDataSource.upsert(todo.toEntity())
    }

    override fun getStream(limit: Int): Flow<List<Todo>> {
        return localDataSource.observeAll(limit).map { todos ->
            withContext(dispatcher) {
                todos.toExternal()
            }
        }
    }

    override fun getStream(limit: Int, searchQuery: String): Flow<List<Todo>> {
        return localDataSource.observeAll(limit, searchQuery).map { todos ->
            withContext(dispatcher) {
                todos.toExternal()
            }
        }
    }

    override suspend fun get(id: String): Todo? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun complete(id: String) {
        localDataSource.updateCompleted(id = id, completed = true)
    }

    override suspend fun activate(id: String) {
        localDataSource.updateCompleted(id = id, completed = false)
    }

    override suspend fun clearCompleted() {
        localDataSource.deleteCompleted()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }

}