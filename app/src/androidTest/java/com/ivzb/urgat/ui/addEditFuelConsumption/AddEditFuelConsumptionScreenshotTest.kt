package com.ivzb.urgat.ui.addEditFuelConsumption

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditFuelConsumptionScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditFuelConsumptionScreen_addFuelConsumption() = screenshotTest(
        uiState = AddEditFuelConsumptionUiState(
            screenTitleResId = R.string.add_fuel_consumption,
        ),
    )

    @Test
    fun screenshotTest_AddEditFuelConsumptionScreen_editFuelConsumption() = screenshotTest(
        uiState = SampleData.SAMPLE_FUEL_CONSUMPTION.first().let { fuelConsumption ->
            AddEditFuelConsumptionUiState(
                screenTitleResId = R.string.edit_fuel_consumption,
                amount = fuelConsumption.amount.toString(),
                price = fuelConsumption.price.toString(),
                currency = fuelConsumption.currency,
                type = fuelConsumption.type,
                carName = fuelConsumption.carName,
                odometer = fuelConsumption.odometer.toString(),
                date = fuelConsumption.date,
                focusIndex = -1,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditFuelConsumptionScreen_loading() = screenshotTest(
        uiState = AddEditFuelConsumptionUiState(
            screenTitleResId = R.string.edit_fuel_consumption,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditFuelConsumptionUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditFuelConsumptionScreen(
                    uiState = uiState,
                    onIntent = {},
                    onBack = {},
                    modifier = Modifier,
                )
            }
        }

        Espresso.closeSoftKeyboard()

        screenshot()
    }

}