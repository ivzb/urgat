package com.ivzb.urgat.ui.addEditFuelConsumption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.fuelConsumption.CreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DeleteFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetLastUsedFuelConsumptionCarNameUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetLastUsedFuelConsumptionCurrencyUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetLastUsedFuelConsumptionTypeUseCase
import com.ivzb.urgat.domain.fuelConsumption.UpdateFuelConsumptionUseCase
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

@HiltViewModel(assistedFactory = AddEditFuelConsumptionViewModel.Factory::class)
class AddEditFuelConsumptionViewModel @AssistedInject constructor(
    @Assisted val route: AddEditFuelConsumptionRoute,
    private val getFuelConsumptionUseCase: GetFuelConsumptionUseCase,
    private val getLastUsedCurrencyUseCase: GetLastUsedFuelConsumptionCurrencyUseCase,
    private val getLastUsedFuelTypeUseCase: GetLastUsedFuelConsumptionTypeUseCase,
    private val getLastUsedCarNameUseCase: GetLastUsedFuelConsumptionCarNameUseCase,
    private val createFuelConsumptionUseCase: CreateFuelConsumptionUseCase,
    private val updateFuelConsumptionUseCase: UpdateFuelConsumptionUseCase,
    private val deleteFuelConsumptionUseCase: DeleteFuelConsumptionUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private var fuelConsumptionId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditFuelConsumptionUiState(
        screenTitleResId = screenTitleResId,
        withDuplicate = fuelConsumptionId != null,
        withClearDefaults = fuelConsumptionId == null,
    ))
    val uiState: StateFlow<AddEditFuelConsumptionUiState> = _uiState.asStateFlow()

    init {
        val id = fuelConsumptionId

        if (id != null) {
            loadFuelConsumption(id)
        } else {
            loadLastUsedDefaults()
        }
    }

    fun onIntent(intent: AddEditFuelConsumptionIntent) {
        when (intent) {
            is AddEditFuelConsumptionIntent.UpdateAmount -> updateAmount(intent.value)
            is AddEditFuelConsumptionIntent.UpdatePrice -> updatePrice(intent.value)
            is AddEditFuelConsumptionIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditFuelConsumptionIntent.UpdateType -> updateType(intent.value)
            is AddEditFuelConsumptionIntent.UpdateCarName -> updateCarName(intent.value)
            is AddEditFuelConsumptionIntent.UpdateOdometer -> updateOdometer(intent.value)
            is AddEditFuelConsumptionIntent.UpdateDate -> updateDate(intent.value)
            AddEditFuelConsumptionIntent.SaveFuelConsumption -> saveFuelConsumption()
            AddEditFuelConsumptionIntent.DeleteFuelConsumption -> deleteFuelConsumption()
            AddEditFuelConsumptionIntent.DuplicateFuelConsumption -> duplicateFuelConsumption()
            AddEditFuelConsumptionIntent.ClearDefaults -> clearDefaults()
            AddEditFuelConsumptionIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amount = amount)
        }
    }

    private fun updatePrice(price: String) {
        _uiState.update {
            it.copy(price = price)
        }
    }

    private fun updateCurrency(currency: String) {
        _uiState.update {
            it.copy(currency = currency)
        }
    }

    private fun updateType(type: String) {
        _uiState.update {
            it.copy(type = type)
        }
    }

    private fun updateCarName(carName: String) {
        _uiState.update {
            it.copy(carName = carName)
        }
    }

    private fun updateOdometer(odometer: String) {
        _uiState.update {
            it.copy(odometer = odometer)
        }
    }

    private fun updateDate(date: Long) {
        _uiState.update {
            it.copy(date = date)
        }
    }

    private fun saveFuelConsumption() {
        val fields = listOf(
            uiState.value.amount,
            uiState.value.price,
            uiState.value.currency,
            uiState.value.type,
            uiState.value.carName,
            uiState.value.odometer,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_fuel_consumption_message
                )
            }

            return
        }

        if (fuelConsumptionId == null) {
            createNewFuelConsumption()
        } else {
            updateFuelConsumption()
        }
    }

    private fun createNewFuelConsumption() = viewModelScope.launch {
        createFuelConsumptionUseCase(
            amount = uiState.value.amount.sanitize().toBigDecimal(),
            price = uiState.value.price.sanitize().toBigDecimal(),
            currency = uiState.value.currency.sanitize(),
            type = uiState.value.type.sanitize(),
            carName = uiState.value.carName.sanitize(),
            odometer = uiState.value.odometer.sanitize().toInt(),
            date = uiState.value.date,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun deleteFuelConsumption() {
        val id = fuelConsumptionId

        if (id == null) {
            Assert.fail("deleteFuelConsumption() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteFuelConsumptionUseCase(id)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun duplicateFuelConsumption() {
        if (fuelConsumptionId == null) {
            Assert.fail("duplicateFuelConsumption() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        fuelConsumptionId = null

        _uiState.update {
            it.copy(
                screenTitleResId = R.string.add_fuel_consumption,
                date = System.currentTimeMillis(),
                withDelete = false,
                withDuplicate = false,
            )
        }
    }

    private fun updateFuelConsumption() {
        val id = fuelConsumptionId

        if (id == null) {
            Assert.fail("updateFuelConsumption() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateFuelConsumptionUseCase(
                id,
                amount = uiState.value.amount.sanitize().toBigDecimal(),
                price = uiState.value.price.sanitize().toBigDecimal(),
                currency = uiState.value.currency.sanitize(),
                type = uiState.value.type.sanitize(),
                carName = uiState.value.carName.sanitize(),
                odometer = uiState.value.odometer.sanitize().toInt(),
                date = uiState.value.date,
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun clearDefaults() {
        _uiState.update {
            it.copy(
                currency = "",
                type = "",
                carName = "",
            )
        }
    }

    private fun loadLastUsedDefaults() {
        viewModelScope.launch {
            val currency = getLastUsedCurrencyUseCase()
            val type = getLastUsedFuelTypeUseCase()
            val carName = getLastUsedCarNameUseCase()

            _uiState.update {
                it.copy(
                    currency = currency ?: it.currency,
                    type = type ?: it.type,
                    carName = carName ?: it.carName,
                )
            }
        }
    }

    private fun loadFuelConsumption(fuelConsumptionId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getFuelConsumptionUseCase(fuelConsumptionId).let { fuelConsumption ->
                if (fuelConsumption != null) {
                    _uiState.update {
                        it.copy(
                            amount = fuelConsumption.amount.toString(),
                            price = fuelConsumption.price.toString(),
                            currency = fuelConsumption.currency,
                            type = fuelConsumption.type,
                            carName = fuelConsumption.carName,
                            odometer = fuelConsumption.odometer.toString(),
                            date = fuelConsumption.date,
                            isLoading = false,
                            withDelete = true,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: AddEditFuelConsumptionRoute): AddEditFuelConsumptionViewModel
    }

}

