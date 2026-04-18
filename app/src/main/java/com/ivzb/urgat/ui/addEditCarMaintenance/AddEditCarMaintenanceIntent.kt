package com.ivzb.urgat.ui.addEditCarMaintenance

sealed class AddEditCarMaintenanceIntent {
    data class UpdateDescription(val value: String): AddEditCarMaintenanceIntent()
    data class UpdatePartNumber(val value: String): AddEditCarMaintenanceIntent()
    data class UpdateUrl(val value: String): AddEditCarMaintenanceIntent()
    data class UpdateCarName(val value: String): AddEditCarMaintenanceIntent()
    data class UpdateOdometer(val value: String): AddEditCarMaintenanceIntent()
    data class UpdateDate(val value: Long): AddEditCarMaintenanceIntent()
    object SaveCarMaintenance: AddEditCarMaintenanceIntent()
    object DeleteCarMaintenance: AddEditCarMaintenanceIntent()
    object DuplicateCarMaintenance: AddEditCarMaintenanceIntent()
    object ClearDefaults: AddEditCarMaintenanceIntent()
    object UserMessageShown: AddEditCarMaintenanceIntent()
}

