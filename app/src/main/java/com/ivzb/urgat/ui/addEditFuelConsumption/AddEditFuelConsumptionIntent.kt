package com.ivzb.urgat.ui.addEditFuelConsumption

sealed class AddEditFuelConsumptionIntent {
    data class UpdateAmount(val value: String): AddEditFuelConsumptionIntent()
    data class UpdatePrice(val value: String): AddEditFuelConsumptionIntent()
    data class UpdateCurrency(val value: String): AddEditFuelConsumptionIntent()
    data class UpdateType(val value: String): AddEditFuelConsumptionIntent()
    data class UpdateCarName(val value: String): AddEditFuelConsumptionIntent()
    data class UpdateOdometer(val value: String): AddEditFuelConsumptionIntent()
    data class UpdateDate(val value: Long): AddEditFuelConsumptionIntent()
    object SaveFuelConsumption: AddEditFuelConsumptionIntent()
    object DeleteFuelConsumption: AddEditFuelConsumptionIntent()
    object DuplicateFuelConsumption: AddEditFuelConsumptionIntent()
    object ClearDefaults: AddEditFuelConsumptionIntent()
    object UserMessageShown: AddEditFuelConsumptionIntent()
}

