package com.ivzb.urgat.data.todo

import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.di.DefaultDispatcher
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

    override suspend fun createTodo(title: String, description: String): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val todoId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val todo = Todo(
            id = todoId,
            title = title,
            description = description,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(todo.toLocal())
        return todoId
    }

    override suspend fun updateTodo(todoId: String, title: String, description: String) {
        val todo = getTodo(todoId)?.copy(
            title = title,
            description = description
        ) ?: throw Exception("Todo (id $todoId) not found")

        localDataSource.upsert(todo.toLocal())
    }

    override suspend fun getTodos(): List<Todo> {
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override fun getTodosStream(): Flow<List<Todo>> {
        return localDataSource.observeAll().map { todos ->
            withContext(dispatcher) {
                todos.toExternal()
            }
        }
    }

    override fun getTodosStream(searchQuery: String): Flow<List<Todo>> {
        return localDataSource.observeAll(searchQuery).map { todos ->
            withContext(dispatcher) {
                todos.toExternal()
            }
        }
    }

    override fun getTodosStream(limit: Int): Flow<List<Todo>> {
        return localDataSource.observeAll(limit).map { todos ->
            withContext(dispatcher) {
                todos.toExternal()
            }
        }
    }

    override fun getTodosStream(limit: Int, searchQuery: String): Flow<List<Todo>> {
        return localDataSource.observeAll(limit, searchQuery).map { todos ->
            withContext(dispatcher) {
                todos.toExternal()
            }
        }
    }

    override fun getTodoStream(todoId: String): Flow<Todo?> {
        return localDataSource.observeById(todoId).map { it.toExternal() }
    }

    override suspend fun getTodo(todoId: String): Todo? {
        return localDataSource.getById(todoId)?.toExternal()
    }

    override suspend fun completeTodo(todoId: String) {
        localDataSource.updateCompleted(todoId = todoId, completed = true)
    }

    override suspend fun activateTodo(todoId: String) {
        localDataSource.updateCompleted(todoId = todoId, completed = false)
    }

    override suspend fun clearCompletedTodos() {
        localDataSource.deleteCompleted()
    }

    override suspend fun deleteAllTodos() {
        localDataSource.deleteAll()
    }

    override suspend fun deleteTodo(todoId: String) {
        localDataSource.deleteById(todoId)
    }

}