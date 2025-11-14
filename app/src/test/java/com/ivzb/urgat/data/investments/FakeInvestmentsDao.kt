package com.ivzb.urgat.data.investments

import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.investment.InvestmentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeInvestmentDao : InvestmentDao {

    private val items = mutableListOf<InvestmentEntity>()
    private val _flow = MutableStateFlow<List<InvestmentEntity>>(emptyList())

    override fun observeAll(limit: Int): Flow<List<InvestmentEntity>> =
        _flow.map { it.take(limit) }

    override fun observeAll(limit: Int, searchQuery: String): Flow<List<InvestmentEntity>> =
        _flow.map { list ->
            list.filter { it.name.contains(searchQuery, ignoreCase = true) }.take(limit)
        }

    override suspend fun getById(id: String): InvestmentEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: InvestmentEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

}