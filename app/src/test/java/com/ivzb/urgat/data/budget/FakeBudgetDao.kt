package com.ivzb.urgat.data.budget

import android.database.Cursor
import android.database.MatrixCursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBudgetDao : BudgetDao {

    private val items = mutableListOf<BudgetEntity>()
    private val itemsFlow = MutableStateFlow<List<BudgetEntity>>(listOf())
    private var itemsCursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<BudgetEntity>> =
        itemsFlow.map { it.filter { it.date in dateStart..dateEnd }.take(limit) }

    override fun stream(): Cursor = itemsCursor

    override suspend fun insertAll(items: List<BudgetEntity>) {
        this.items.addAll(items)
        itemsFlow.value = items.toList()
    }

    override suspend fun getById(id: String): BudgetEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        itemsFlow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: BudgetEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        itemsFlow.value = items.toList()
    }

    override suspend fun getLastUsedCurrency(): String? =
        items.filter { !it.isDeleted }.maxByOrNull { it.createdAt }?.currency

}
