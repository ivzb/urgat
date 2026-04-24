package com.ivzb.urgat.data.trade

import android.database.Cursor
import android.database.MatrixCursor
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTradeDao : TradeDao {

    private val items = mutableListOf<TradeEntity>()
    private val _flow = MutableStateFlow<List<TradeEntity>>(listOf())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<TradeEntity>> =
        _flow.map { it.filter { it.date in dateStart..dateEnd }.take(if (limit == -1) Int.MAX_VALUE else limit) }

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<TradeEntity>> =
        _flow.map { list ->
            list.filter { it.date in dateStart..dateEnd && it.name.contains(searchQuery, ignoreCase = true) }
                .take(if (limit == -1) Int.MAX_VALUE else limit)
        }

    override fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<TradeEntity>> =
        _flow

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<TradeEntity>) {
        this.items.addAll(items)
        _flow.value = this.items.toList()
    }

    override suspend fun getById(id: String): TradeEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: TradeEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

    override suspend fun getLastUsedCurrency(): String? =
        items.filter { !it.isDeleted }.maxByOrNull { it.createdAt }?.currency

    override suspend fun getLastUsedName(): String? =
        items.filter { !it.isDeleted }.maxByOrNull { it.createdAt }?.name

    override suspend fun getLastUsedFee(): String? =
        items.filter { !it.isDeleted }.maxByOrNull { it.createdAt }?.fee?.toString()

}

