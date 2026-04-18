package com.ivzb.urgat.ui.addEditCarMaintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.carMaintenance.CreateCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.DeleteCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.GetCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.GetLastUsedCarMaintenanceCarNameUseCase
import com.ivzb.urgat.domain.carMaintenance.UpdateCarMaintenanceUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.sanitize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditCarMaintenanceViewModel.Factory::class)
class AddEditCarMaintenanceViewModel @AssistedInject constructor(
    @Assisted val route: AddEditCarMaintenanceRoute,
    private val getCarMaintenanceUseCase: GetCarMaintenanceUseCase,
    private val getLastUsedCarNameUseCase: GetLastUsedCarMaintenanceCarNameUseCase,
    private val createCarMaintenanceUseCase: CreateCarMaintenanceUseCase,
    private val updateCarMaintenanceUseCase: UpdateCarMaintenanceUseCase,
    private val deleteCarMaintenanceUseCase: DeleteCarMaintenanceUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private val carMaintenanceId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditCarMaintenanceUiState(
        screenTitleResId = screenTitleResId,
        withDuplicate = carMaintenanceId != null,
        withClearDefaults = carMaintenanceId == null,
    ))
    val uiState: StateFlow<AddEditCarMaintenanceUiState> = _uiState.asStateFlow()

    init {
        if (carMaintenanceId != null) {
            loadCarMaintenance(carMaintenanceId)
        } else {
            loadLastUsedDefaults()
        }
    }

    fun onIntent(intent: AddEditCarMaintenanceIntent) {
        when (intent) {
            is AddEditCarMaintenanceIntent.UpdateDescription -> updateDescription(intent.value)
            is AddEditCarMaintenanceIntent.UpdatePartNumber -> updatePartNumber(intent.value)
            is AddEditCarMaintenanceIntent.UpdateUrl -> updateUrl(intent.value)
            is AddEditCarMaintenanceIntent.UpdateCarName -> updateCarName(intent.value)
            is AddEditCarMaintenanceIntent.UpdateOdometer -> updateOdometer(intent.value)
            is AddEditCarMaintenanceIntent.UpdateDate -> updateDate(intent.value)
            AddEditCarMaintenanceIntent.SaveCarMaintenance -> saveCarMaintenance()
            AddEditCarMaintenanceIntent.DeleteCarMaintenance -> deleteCarMaintenance()
            AddEditCarMaintenanceIntent.DuplicateCarMaintenance -> duplicateCarMaintenance()
            AddEditCarMaintenanceIntent.ClearDefaults -> clearDefaults()
            AddEditCarMaintenanceIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    private fun updatePartNumber(partNumber: String) {
        _uiState.update { it.copy(partNumber = partNumber) }
    }

    private fun updateUrl(url: String) {
        _uiState.update { it.copy(url = url) }
    }

    private fun updateCarName(carName: String) {
        _uiState.update { it.copy(carName = carName) }
    }

    private fun updateOdometer(odometer: String) {
        _uiState.update { it.copy(odometer = odometer) }
    }

    private fun updateDate(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    private fun saveCarMaintenance() {
        val fields = listOf(
            uiState.value.description,
            uiState.value.carName,
            uiState.value.odometer,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_car_maintenance_message
                )
            }
            return
        }

        if (carMaintenanceId == null) {
            createNewCarMaintenance()
        } else {
            updateCarMaintenance()
        }
    }

    private fun createNewCarMaintenance() = viewModelScope.launch {
        createCarMaintenanceUseCase(
            description = uiState.value.description.sanitize(),
            partNumber = uiState.value.partNumber.sanitize(),
            url = uiState.value.url.sanitize().ifEmpty { null },
            carName = uiState.value.carName.sanitize(),
            odometer = uiState.value.odometer.sanitize().toInt(),
            date = uiState.value.date,
        )

        _uiState.update { it.copy(shouldNavigateBack = true) }
    }

    private fun deleteCarMaintenance() {
        if (carMaintenanceId == null) {
            Assert.fail("deleteCarMaintenance() was called but id is null.")
            _uiState.update { it.copy(userMessage = R.string.something_went_wrong) }
            return
        }

        viewModelScope.launch {
            deleteCarMaintenanceUseCase(carMaintenanceId)
            _uiState.update { it.copy(shouldNavigateBack = true) }
        }
    }

    private fun duplicateCarMaintenance() {
        if (carMaintenanceId == null) {
            Assert.fail("duplicateCarMaintenance() was called but id is null.")
            _uiState.update { it.copy(userMessage = R.string.something_went_wrong) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(date = System.currentTimeMillis()) }
            createNewCarMaintenance()
        }
    }

    private fun updateCarMaintenance() {
        if (carMaintenanceId == null) {
            Assert.fail("updateCarMaintenance() was called but id is null.")
            _uiState.update { it.copy(userMessage = R.string.something_went_wrong) }
            return
        }

        viewModelScope.launch {
            updateCarMaintenanceUseCase(
                carMaintenanceId,
                description = uiState.value.description.sanitize(),
                partNumber = uiState.value.partNumber.sanitize(),
                url = uiState.value.url.sanitize().ifEmpty { null },
                carName = uiState.value.carName.sanitize(),
                odometer = uiState.value.odometer.sanitize().toInt(),
                date = uiState.value.date,
            )

            _uiState.update { it.copy(shouldNavigateBack = true) }
        }
    }

    private fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun clearDefaults() {
        _uiState.update { it.copy(carName = "") }
    }

    private fun loadLastUsedDefaults() {
        viewModelScope.launch {
            val carName = getLastUsedCarNameUseCase()
            _uiState.update {
                it.copy(carName = carName ?: it.carName)
            }
        }
    }

    private fun loadCarMaintenance(carMaintenanceId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            getCarMaintenanceUseCase(carMaintenanceId).let { carMaintenance ->
                if (carMaintenance != null) {
                    _uiState.update {
                        it.copy(
                            description = carMaintenance.description,
                            partNumber = carMaintenance.partNumber ?: "",
                            url = carMaintenance.url ?: "",
                            carName = carMaintenance.carName,
                            odometer = carMaintenance.odometer.toString(),
                            date = carMaintenance.date,
                            isLoading = false,
                            withDelete = true,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: AddEditCarMaintenanceRoute): AddEditCarMaintenanceViewModel
    }

}

