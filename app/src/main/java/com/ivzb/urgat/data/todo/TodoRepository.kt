package com.ivzb.urgat.data.todo

import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    fun getTodosStream(): Flow<List<Todo>>

    suspend fun getTodos(): List<Todo>

    fun getTodoStream(todoId: String): Flow<Todo?>

    suspend fun getTodo(todoId: String): Todo?

    suspend fun createTodo(title: String, description: String): String

    suspend fun updateTodo(todoId: String, title: String, description: String)

    suspend fun completeTodo(todoId: String)

    suspend fun activateTodo(todoId: String)

    suspend fun clearCompletedTodos()

    suspend fun deleteAllTodos()

    suspend fun deleteTodo(todoId: String)
}
