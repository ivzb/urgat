package com.ivzb.urgat.data.carMaintenance

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultCarMaintenanceRepository @Inject constructor(
    private val localDataSource: CarMaintenanceDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : CarMaintenanceRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<CarMaintenance>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { items ->
            withContext(dispatcher) {
                items.map { it.toExternal() }
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<CarMaintenance>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { items ->
            withContext(dispatcher) {
                items.map { it.toExternal() }
            }
        }
    }

    override suspend fun create(
        description: String,
        partNumber: String?,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    ): String {
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }

        val carMaintenance = CarMaintenance(
            id = id,
            description = description,
            partNumber = partNumber,
            url = url,
            carName = carName,
            odometer = odometer,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )

        localDataSource.upsert(carMaintenance.toEntity())

        return id
    }

    override suspend fun update(
        id: String,
        description: String,
        partNumber: String?,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        val carMaintenance = get(id)?.copy(
            description = description,
            partNumber = partNumber,
            url = url,
            carName = carName,
            odometer = odometer,
            date = date,
        )

        if (carMaintenance == null) {
            Assert.fail("CarMaintenance (id $id) not found while update")
            return
        }

        localDataSource.upsert(carMaintenance.toEntity())
    }

    override suspend fun get(id: String): CarMaintenance? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }

    override suspend fun getLastUsedCarName(): String? {
        return localDataSource.getLastUsedCarName()
    }

}

