package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.Trade
import java.math.BigDecimal

class FakeGetTradeUseCase : GetTradeUseCase {

    var toReturn: Trade? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Trade? {
        calledWithId = id
        return toReturn
    }
}

class FakeGetLastUsedTradeCurrencyUseCase : GetLastUsedTradeCurrencyUseCase {

    var toReturn: String? = null

    override suspend fun invoke(): String? {
        return toReturn
    }
}

class FakeGetLastUsedTradeNameUseCase : GetLastUsedTradeNameUseCase {

    var toReturn: String? = null

    override suspend fun invoke(): String? {
        return toReturn
    }
}

class FakeGetLastUsedTradeFeeUseCase : GetLastUsedTradeFeeUseCase {

    var toReturn: String? = null

    override suspend fun invoke(): String? {
        return toReturn
    }
}

class FakeCreateTradeUseCase : CreateTradeUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        name: String,
        shares: BigDecimal,
        fee: BigDecimal,
        marketPrice: BigDecimal,
        currency: String,
        orderType: String,
        date: Long,
    ) {
        created = listOf(name, shares, fee, marketPrice, currency, orderType, date)
    }
}

class FakeUpdateTradeUseCase : UpdateTradeUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        name: String,
        shares: BigDecimal,
        fee: BigDecimal,
        marketPrice: BigDecimal,
        currency: String,
        orderType: String,
        date: Long,
    ) {
        updated = listOf(id, name, shares, fee, marketPrice, currency, orderType, date)
    }
}

class FakeDeleteTradeUseCase : DeleteTradeUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(id: String) {
        deleted = listOf(id)
    }
}

