package com.ivzb.urgat.data.expenses

import android.database.Cursor
import android.database.MatrixCursor
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.expense.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeExpenseDao : ExpenseDao {

    private val items = mutableListOf<ExpenseEntity>()
    private val _flow = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int): Flow<List<ExpenseEntity>> =
        _flow.map { it.take(limit) }

    override fun observeAll(limit: Int, searchQuery: String): Flow<List<ExpenseEntity>> =
        _flow.map { list ->
            list.filter { it.name.contains(searchQuery, ignoreCase = true) }.take(limit)
        }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<ExpenseEntity>) {
        this.items.addAll(items)
        _flow.value = items.toList()
    }

    override suspend fun getById(id: String): ExpenseEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: ExpenseEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

}