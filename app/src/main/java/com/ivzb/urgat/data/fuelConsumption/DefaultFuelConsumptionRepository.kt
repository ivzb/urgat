package com.ivzb.urgat.data.fuelConsumption

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class DefaultFuelConsumptionRepository @Inject constructor(
    private val localDataSource: FuelConsumptionDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : FuelConsumptionRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<FuelConsumption>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { fuelConsumption ->
            withContext(dispatcher) {
                fuelConsumption.toExternal()
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<FuelConsumption>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { fuelConsumption ->
            withContext(dispatcher) {
                fuelConsumption.toExternal()
            }
        }
    }

    override suspend fun create(
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        type: String,
        carName: String,
        odometer: Int,
        date: Long,
    ): String {
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }

        val fuelConsumption = FuelConsumption(
            id = id,
            amount = amount,
            price = price,
            currency = currency,
            type = type,
            carName = carName,
            odometer = odometer,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )

        localDataSource.upsert(fuelConsumption.toEntity())
        
        return id
    }

    override suspend fun update(
        id: String,
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        type: String,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        val fuelConsumption = get(id)?.copy(
            amount = amount,
            price = price,
            currency = currency,
            type = type,
            carName = carName,
            odometer = odometer,
            date = date,
        )

        if (fuelConsumption == null) {
            Assert.fail("FuelConsumption (id $id) not found while update")
            return
        }

        localDataSource.upsert(fuelConsumption.toEntity())
    }

    override suspend fun get(id: String): FuelConsumption? {
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }

    override suspend fun getLastUsedCurrency(): String? {
        return localDataSource.getLastUsedCurrency()
    }

    override suspend fun getLastUsedType(): String? {
        return localDataSource.getLastUsedType()
    }

    override suspend fun getLastUsedCarName(): String? {
        return localDataSource.getLastUsedCarName()
    }
}

