package com.ivzb.urgat.data.saving

import android.database.Cursor
import android.database.MatrixCursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSavingDao : SavingDao {

    private val items = mutableListOf<SavingEntity>()
    private val _flow = MutableStateFlow<List<SavingEntity>>(emptyList())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int): Flow<List<SavingEntity>> =
        _flow.map { it.take(limit) }

    override fun observeAll(limit: Int, searchQuery: String): Flow<List<SavingEntity>> =
        _flow.map { list ->
            list.filter { it.name.contains(searchQuery, ignoreCase = true) }.take(limit)
        }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<SavingEntity>) {
        this.items.addAll(items)
        _flow.value = items.toList()
    }

    override suspend fun getById(id: String): SavingEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: SavingEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

}