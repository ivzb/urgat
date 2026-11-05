package com.ivzb.urgat.data.investment

import android.database.Cursor
import android.database.MatrixCursor
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeInvestmentDao : InvestmentDao {

    private val items = mutableListOf<InvestmentEntity>()
    private val itemsFlow = MutableStateFlow<List<InvestmentEntity>>(listOf())
    private var itemsCursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<InvestmentEntity>> =
        itemsFlow.map { it.filter { it.date in dateStart..dateEnd }.take(limit) }

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<InvestmentEntity>> =
        itemsFlow.map { list ->
            list.filter { it.date in dateStart..dateEnd && it.name.contains(searchQuery, ignoreCase = true) }.take(limit)
        }

    override fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<InvestmentEntity>> =
        itemsFlow

    override fun stream(): Cursor = itemsCursor

    override suspend fun insertAll(items: List<InvestmentEntity>) {
        this.items.addAll(items)
        itemsFlow.value = items.toList()
    }

    override suspend fun getById(id: String): InvestmentEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        itemsFlow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: InvestmentEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        itemsFlow.value = items.toList()
    }

    override suspend fun getLastUsedCurrency(): String? =
        items.filter { !it.isDeleted }.maxByOrNull { it.createdAt }?.currency

}
