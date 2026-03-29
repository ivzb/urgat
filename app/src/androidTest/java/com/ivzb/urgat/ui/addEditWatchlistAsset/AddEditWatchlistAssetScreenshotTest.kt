package com.ivzb.urgat.ui.addEditWatchlistAsset

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

@Screenshot
class AddEditWatchlistAssetScreenshotTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditWatchlistAssetScreen_addWatchlistAsset() = screenshotTest(
        uiState = AddEditWatchlistAssetUiState(
            screenTitleResId = R.string.add_watchlist_asset,
        ),
    )

    @Test
    fun screenshotTest_AddEditWatchlistAssetScreen_editWatchlistAsset() = screenshotTest(
        uiState = AddEditWatchlistAssetUiState(
            screenTitleResId = R.string.edit_watchlist_asset,
            name = "GOOGL",
            focusIndex = -1,
        ),
    )

    @Test
    fun screenshotTest_AddEditWatchlistAssetScreen_loading() = screenshotTest(
        uiState = AddEditWatchlistAssetUiState(
            screenTitleResId = R.string.edit_watchlist_asset,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditWatchlistAssetUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditWatchlistAssetScreen(
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

