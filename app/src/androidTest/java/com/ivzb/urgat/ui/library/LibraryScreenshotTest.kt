package com.ivzb.urgat.ui.library

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.canTakeScreenshot
import androidx.test.core.app.takeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class LibraryScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    var nameRule = TestName()

    @Test
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

    private fun screenshot() {
        if (canTakeScreenshot()) {
            takeScreenshot().writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
        } else {
            throw IllegalStateException("Screenshot cannot be taken.")
        }

        // wait for screenshot to be saved
        Thread.sleep(250)
    }

}