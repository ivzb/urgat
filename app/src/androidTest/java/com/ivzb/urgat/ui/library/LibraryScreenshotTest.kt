package com.ivzb.urgat.ui.library

import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

class LibraryScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_LibraryScreen_sampleData() = screenshotTest(
        uiState = LibraryUiState(
            todos = SampleData.SAMPLE_TODOS.take(3),
            notes = SampleData.SAMPLE_NOTES.take(3),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_LibraryScreen_emptyState() = screenshotTest(
        uiState = LibraryUiState(
            todos = listOf(),
            notes = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_LibraryScreen_loadingState() = screenshotTest(
        uiState = LibraryUiState(
            todos = listOf(),
            notes = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_LibraryScreen_errorState() = screenshotTest(
        uiState = LibraryUiState(
            todos = listOf(),
            notes = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: LibraryUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                LibraryScreen(
                    uiState = uiState,
                    onIntent = {},
                    onSeeAllTodos = {},
                    onTodoClick = {},
                    onSeeAllNotes = {},
                    onNoteClick = {},
                    navController = rememberNavController(),
                    modifier = Modifier,
                )
            }
        }

        screenshot()
    }

}