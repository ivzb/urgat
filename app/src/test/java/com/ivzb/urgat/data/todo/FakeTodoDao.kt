package com.ivzb.urgat.data.todo

import android.database.Cursor
import android.database.MatrixCursor
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTodoDao : TodoDao {

    private val items = mutableListOf<TodoEntity>()
    private val itemsFlow = MutableStateFlow<List<TodoEntity>>(listOf())
    private var itemsCursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<TodoEntity>> =
        itemsFlow.map { it.filter { it.createdAt in dateStart..dateEnd }.take(limit) }

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<TodoEntity>> =
        itemsFlow.map { list ->
            list.filter { it.createdAt in dateStart..dateEnd && it.content.contains(searchQuery, ignoreCase = true) }
        }

    override fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<TodoEntity>> =
        itemsFlow

    override fun stream(): Cursor = itemsCursor

    override suspend fun insertAll(items: List<TodoEntity>) {
        this.items.addAll(items)
        itemsFlow.value = items.toList()
    }

    override suspend fun getById(id: String): TodoEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        itemsFlow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: TodoEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        itemsFlow.value = items.toList()
    }

    override suspend fun updateCompleted(id: String, completed: Boolean) {
        val item = items.first { it.id == id }
        items[items.indexOf(item)] = item.copy(isCompleted = completed)
    }

    override suspend fun deleteCompleted(): Int {
        items.removeAll { it.isCompleted }
        itemsFlow.value = items.toList()
        return 1
    }

}
