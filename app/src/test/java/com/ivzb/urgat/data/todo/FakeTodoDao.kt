package com.ivzb.urgat.data.todo

import android.database.Cursor
import android.database.MatrixCursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTodoDao : TodoDao {

    private val items = mutableListOf<TodoEntity>()
    private val _flow = MutableStateFlow<List<TodoEntity>>(emptyList())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int): Flow<List<TodoEntity>> =
        _flow.map { it.take(limit) }

    override fun observeAll(limit: Int, searchQuery: String): Flow<List<TodoEntity>> =
        _flow.map { list ->
            list.filter { it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
        }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<TodoEntity>) {
        this.items.addAll(items)
        _flow.value = items.toList()
    }

    override suspend fun getById(id: String): TodoEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: TodoEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

    override suspend fun updateCompleted(id: String, completed: Boolean) {
        val item = items.first { it.id == id }
        items[items.indexOf(item)] = item.copy(isCompleted = completed)
    }

    override suspend fun deleteCompleted(): Int {
        items.removeAll { it.isCompleted }
        _flow.value = items.toList()
        return 1
    }

}
