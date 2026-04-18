package com.ivzb.urgat.ui.car

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceUiState
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class CarScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_CarScreen_sampleData_fuelConsumption_transactions() =
        screenshotTest_CarScreen_sampleData(initialPage = 0)

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_CarScreen_sampleData_carMaintenance_transactions() =
        screenshotTest_CarScreen_sampleData(initialPage = 1)

    private fun screenshotTest_CarScreen_sampleData(
        initialPage: Int,
    ) = screenshotTest(
        uiState = CarUiState(
            fuelConsumptionUiState = FuelConsumptionUiState(
                fuelConsumption = SampleData.SAMPLE_FUEL_CONSUMPTION_GROUPED,
            ),
            carMaintenanceUiState = CarMaintenanceUiState(
                carMaintenance = SampleData.SAMPLE_CAR_MAINTENANCE_GROUPED,
            ),
            isLoading = false,
            initialPage = initialPage,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_CarScreen_emptyState() = screenshotTest(
        uiState = CarUiState(
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_CarScreen_loadingState() = screenshotTest(
        uiState = CarUiState(
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_CarScreen_errorState() = screenshotTest(
        uiState = CarUiState(
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: CarUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                CarScreen(
                    uiState = uiState,
                    onIntent = {},
                    
                    onMoreFuelConsumption = {},
                    onFuelConsumptionClick = {},

                    onMoreCarMaintenance = {},
                    onCarMaintenanceClick = {},

                    navigator = FakeNavigator(),

                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}