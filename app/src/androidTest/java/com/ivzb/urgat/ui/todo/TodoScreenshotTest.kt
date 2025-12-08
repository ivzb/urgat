package com.ivzb.urgat.ui.todo

import androidx.compose.ui.Modifier
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

class TodoScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_TodoScreen_sampleData() = screenshotTest(
        uiState = TodoUiState(
            items = SampleData.SAMPLE_TODOS,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TodoScreen_emptyState() = screenshotTest(
        uiState = TodoUiState(
            items = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TodoScreen_loadingState() = screenshotTest(
        uiState = TodoUiState(
            items = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TodoScreen_errorState() = screenshotTest(
        uiState = TodoUiState(
            items = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: TodoUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                TodoScreen(
                    uiState = uiState,
                    onIntent = {},
                    onAddTodo = {},
                    onTodoClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}