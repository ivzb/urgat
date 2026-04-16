package com.ivzb.urgat.data.fuelConsumption

import android.database.Cursor
import android.database.MatrixCursor
import com.ivzb.urgat.data.expense.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFuelConsumptionDao : FuelConsumptionDao {

    private val items = mutableListOf<FuelConsumptionEntity>()
    private val _flow = MutableStateFlow<List<FuelConsumptionEntity>>(listOf())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<FuelConsumptionEntity>> =
        _flow.map { it.filter { e -> !e.isDeleted && e.date in dateStart..dateEnd }.sortedByDescending { it.date }.take(limit) }

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<FuelConsumptionEntity>> =
        _flow.map { it.filter { e -> !e.isDeleted && e.date in dateStart..dateEnd && e.carName.contains(searchQuery, ignoreCase = true) }.sortedByDescending { it.date }.take(limit) }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<FuelConsumptionEntity>) {
        this.items.addAll(items)
        _flow.value = this.items.toList()
    }

    override suspend fun upsert(entity: FuelConsumptionEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

    override suspend fun getById(id: String): FuelConsumptionEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun getLatestDate(): Long? = getLastUsed()?.date

    override suspend fun getLastUsedCurrency(): String? = getLastUsed()?.currency

    override suspend fun getLastUsedType(): String? = getLastUsed()?.type

    override suspend fun getLastUsedCarName(): String? = getLastUsed()?.carName

    private fun getLastUsed(): FuelConsumptionEntity? =
        items.filter { !it.isDeleted }.maxByOrNull { it.date }

}

