package com.ivzb.urgat.ui.todos

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class TodosScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_TodosScreen_sampleData() = screenshotTest(
        uiState = TodosUiState(
            items = SampleData.SAMPLE_TODOS,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TodosScreen_emptyState() = screenshotTest(
        uiState = TodosUiState(
            items = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TodosScreen_loadingState() = screenshotTest(
        uiState = TodosUiState(
            items = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_TodosScreen_errorState() = screenshotTest(
        uiState = TodosUiState(
            items = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: TodosUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                TodosScreen(
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