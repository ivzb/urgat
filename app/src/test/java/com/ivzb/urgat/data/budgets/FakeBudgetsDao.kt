package com.ivzb.urgat.data.budgets

import android.database.Cursor
import android.database.MatrixCursor
import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.budget.BudgetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBudgetDao : BudgetDao {

    private val items = mutableListOf<BudgetEntity>()
    private val _flow = MutableStateFlow<List<BudgetEntity>>(emptyList())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int): Flow<List<BudgetEntity>> =
        _flow.map { it.take(limit) }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<BudgetEntity>) {
        this.items.addAll(items)
        _flow.value = items.toList()
    }

    override suspend fun getById(id: String): BudgetEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: BudgetEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

}