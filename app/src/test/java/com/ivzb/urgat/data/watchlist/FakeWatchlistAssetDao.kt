package com.ivzb.urgat.data.watchlist

import android.database.Cursor
import android.database.MatrixCursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeWatchlistAssetDao : WatchlistAssetDao {

    private val items = mutableListOf<WatchlistAssetEntity>()
    private val _flow = MutableStateFlow<List<WatchlistAssetEntity>>(listOf())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<WatchlistAssetEntity>> =
        _flow.map { it.filter { it.createdAt in dateStart..dateEnd }.take(limit) }

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<WatchlistAssetEntity>> =
        _flow.map { list ->
            list.filter { it.createdAt in dateStart..dateEnd && it.name.contains(searchQuery, ignoreCase = true) }.take(limit)
        }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<WatchlistAssetEntity>) {
        this.items.addAll(items)
        _flow.value = this.items.toList()
    }

    override suspend fun getById(id: String): WatchlistAssetEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: WatchlistAssetEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

}

