package com.ivzb.urgat.ui.fuelConsumption

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class FuelConsumptionScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_FuelConsumptionScreen_sampleData_monthly() =
        screenshotTest_FuelConsumptionScreen_sampleData(initialPage = 0)

    @Test
    fun screenshotTest_FuelConsumptionScreen_sampleData_yearly() =
        screenshotTest_FuelConsumptionScreen_sampleData(initialPage = 1)

    @Test
    fun screenshotTest_FuelConsumptionScreen_sampleData_allTime() =
        screenshotTest_FuelConsumptionScreen_sampleData(initialPage = 2)

    private fun screenshotTest_FuelConsumptionScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = FuelConsumptionUiState(
            fuelConsumption = SampleData.SAMPLE_FUEL_CONSUMPTION_GROUPED,
            categoriesMonthly = SampleData.SAMPLE_FUEL_CONSUMPTION_CATEGORIES_MONTHLY,
            categoriesYearly = SampleData.SAMPLE_FUEL_CONSUMPTION_CATEGORIES_YEARLY,
            categoriesAllTime = SampleData.SAMPLE_FUEL_CONSUMPTION_CATEGORIES_ALL_TIME,
            isLoading = false,
            hasError = false,
            initialPage = initialPage,
        ),
    )

    @Test
    fun screenshotTest_FuelConsumptionScreen_emptyState() = screenshotTest(
        uiState = FuelConsumptionUiState(
            fuelConsumption = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FuelConsumptionScreen_loadingState() = screenshotTest(
        uiState = FuelConsumptionUiState(
            fuelConsumption = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_FuelConsumptionScreen_errorState() = screenshotTest(
        uiState = FuelConsumptionUiState(
            fuelConsumption = listOf(),
            categoriesMonthly = listOf(),
            categoriesYearly = listOf(),
            categoriesAllTime = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: FuelConsumptionUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                FuelConsumptionScreen(
                    uiState = uiState,
                    onIntent = {},
                    onFuelConsumptionClick = {},
                    onBack = {},
                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}

