package com.ivzb.urgat.data.carMaintenance

import android.database.Cursor
import android.database.MatrixCursor
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCarMaintenanceDao : CarMaintenanceDao {

    private val items = mutableListOf<CarMaintenanceEntity>()
    private val itemsFlow = MutableStateFlow<List<CarMaintenanceEntity>>(listOf())
    private var itemsCursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<CarMaintenanceEntity>> =
        itemsFlow.map { it.filter { e -> !e.isDeleted && e.date in dateStart..dateEnd }.sortedByDescending { it.date }.take(limit) }

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<CarMaintenanceEntity>> =
        itemsFlow.map { it.filter { e -> !e.isDeleted && e.date in dateStart..dateEnd && e.description.contains(searchQuery, ignoreCase = true) }.sortedByDescending { it.date }.take(limit) }

    override fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<CarMaintenanceEntity>> =
        itemsFlow

    override fun stream(): Cursor = itemsCursor

    override suspend fun insertAll(items: List<CarMaintenanceEntity>) {
        this.items.addAll(items)
        itemsFlow.value = this.items.toList()
    }

    override suspend fun upsert(entity: CarMaintenanceEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        itemsFlow.value = items.toList()
    }

    override suspend fun getById(id: String): CarMaintenanceEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        itemsFlow.value = items.toList()
        return 1
    }

    override suspend fun getLatestDate(): Long? = getLastUsed()?.date

    override suspend fun getLastUsedCarName(): String? = getLastUsed()?.carName

    private fun getLastUsed(): CarMaintenanceEntity? =
        items.filter { !it.isDeleted }.maxByOrNull { it.date }
}
