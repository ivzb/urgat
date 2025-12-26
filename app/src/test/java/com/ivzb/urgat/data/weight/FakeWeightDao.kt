package com.ivzb.urgat.data.weight

import android.database.Cursor
import android.database.MatrixCursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class FakeWeightDao: WeightDao {

    private val items = mutableListOf<WeightEntity>()
    private val _flow = MutableStateFlow<List<WeightEntity>>(emptyList())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int): Flow<List<WeightEntity>> =
        _flow.map { it.take(limit) }

    override fun observeAll(limit: Int, from: Long, to: Long): Flow<List<WeightEntity>> =
        _flow.map { it.take(limit) }
            .filter { it.all { it.createdAt in from..to } }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<WeightEntity>) {
        this.items.addAll(items)
        _flow.value = items.toList()
    }

    override suspend fun getById(id: String): WeightEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: WeightEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

}
