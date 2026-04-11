package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.investmentMarketPrice.InvestmentMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.util.Assert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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

    private val _isRefreshingFlow = MutableStateFlow(false)
    override val isRefreshingFlow: StateFlow<Boolean> = _isRefreshingFlow.asStateFlow()

    private var refreshStartTime = 0L

    override fun observe(limit: Int, dateRange: LongRange): Flow<List<Investment>> {
        return marketPriceRepository.refreshFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                _isRefreshingFlow.value = true
                refreshStartTime = System.currentTimeMillis()

                localDataSource.observeAll(limit, dateRange.first, dateRange.last).map { investments ->
                    withContext(dispatcher) {
                        investments.toExternal()
                    }
                }.onEach {
                    ensureMinRefreshDuration()
                    _isRefreshingFlow.value = false
                }
            }
    }

    override fun observe(limit: Int, dateRange: LongRange, searchQuery: String): Flow<List<Investment>> {
        return marketPriceRepository.refreshFlow
            .onStart { emit(Unit) }
            .flatMapLatest {
                _isRefreshingFlow.value = true
                refreshStartTime = System.currentTimeMillis()

                localDataSource.observeAll(limit, dateRange.first, dateRange.last, searchQuery).map { investments ->
                    withContext(dispatcher) {
                        investments.toExternal()
                    }
                }.onEach {
                    ensureMinRefreshDuration()
                    _isRefreshingFlow.value = false
                }
            }
    }

    private suspend fun ensureMinRefreshDuration() {
        val elapsed = System.currentTimeMillis() - refreshStartTime

        if (elapsed < MIN_REFRESH_DURATION_MS) {
            delay(MIN_REFRESH_DURATION_MS - elapsed)
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
        return localDataSource.getById(id)?.toExternal()
    }

    override suspend fun delete(id: String) {
        localDataSource.deleteById(id)
    }

    override suspend fun getLastUsedCurrency(): String? {
        return localDataSource.getLastUsedCurrency()
    }

    private suspend fun List<InvestmentEntity>.toExternal(): List<Investment> {
        return coroutineScope { map { async { it.toExternal() } }.awaitAll() }
    }

    private suspend fun InvestmentEntity.toExternal(): Investment {
        val marketPrice = marketPriceRepository.fetchPrice(name)

        return toExternal(
            description = marketPrice?.description,
            marketPrice = investmentMarketPriceRepository.calculateMarketPrice(name, currency),
            pricePrecision = marketPrice?.pricePrecision,
        )
    }

    companion object {

        private const val MIN_REFRESH_DURATION_MS = 1_000L

    }

}