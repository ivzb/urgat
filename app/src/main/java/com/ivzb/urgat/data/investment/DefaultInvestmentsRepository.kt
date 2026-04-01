package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.investmentMarketPrice.InvestmentMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class DefaultInvestmentsRepository @Inject constructor(
    private val localDataSource: InvestmentDao,
    private val marketPriceRepository: MarketPriceRepository,
    private val investmentMarketPriceRepository: InvestmentMarketPriceRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : InvestmentsRepository {

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Investment>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { investments ->
            withContext(dispatcher) {
                investments.toExternal(marketPriceRepository::fetchPrice, investmentMarketPriceRepository::calculateMarketPrice)
            }
        }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Investment>> {
        return localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { investments ->
            withContext(dispatcher) {
                investments.toExternal(marketPriceRepository::fetchPrice, investmentMarketPriceRepository::calculateMarketPrice)
            }
        }
    }

    override suspend fun create(
        name: String,
        shares: BigDecimal,
        costPrice: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ): String {
        // ID creation might be a complex operation so it's executed using the supplied
        // coroutine dispatcher
        val id = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val investment = Investment(
            id = id,
            name = name,
            description = null,
            shares = shares,
            costPrice = costPrice,
            marketPrice = BigDecimal.ZERO,
            category = category,
            currency = currency,
            date = date,
            isDeleted = false,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.upsert(investment.toEntity())
        return id
    }

    override suspend fun update(
        id: String,
        name: String,
        shares: BigDecimal,
        costPrice: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {
        val investment = get(id)?.copy(
            name = name,
            shares = shares,
            costPrice = costPrice,
            category = category,
            currency = currency,
            date = date,
        )

        if (investment == null) {
            Assert.fail("Investment (id $id) not found while update")
            return
        }

        localDataSource.upsert(investment.toEntity())
    }

    override suspend fun get(id: String): Investment? {
        return localDataSource.getById(id)?.toExternal(
            marketPriceRepository::fetchPrice,
            investmentMarketPriceRepository::calculateMarketPrice,
        )
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }
}
