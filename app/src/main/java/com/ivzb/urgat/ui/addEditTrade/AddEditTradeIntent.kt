package com.ivzb.urgat.ui.addEditTrade

sealed class AddEditTradeIntent {
    data class UpdateName(val value: String): AddEditTradeIntent()
    data class UpdateShares(val value: String): AddEditTradeIntent()
    data class UpdateFee(val value: String): AddEditTradeIntent()
    data class UpdateMarketPrice(val value: String): AddEditTradeIntent()
    data class UpdateCurrency(val value: String): AddEditTradeIntent()
    data class UpdateOrderType(val value: String): AddEditTradeIntent()
    data class UpdateDate(val value: Long): AddEditTradeIntent()
    object SaveTrade: AddEditTradeIntent()
    object DeleteTrade: AddEditTradeIntent()
    object DuplicateTrade: AddEditTradeIntent()
    object UserMessageShown: AddEditTradeIntent()
}

