package com.ivzb.urgat.ui.library

import androidx.compose.ui.Modifier
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.notes.NotesUiState
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosUiState
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class LibraryScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_LibraryScreen_sampleData_todos() =
        screenshotTest_LibraryScreen_sampleData(initialPage = 0)

    @Test
    fun screenshotTest_LibraryScreen_sampleData_notes() =
        screenshotTest_LibraryScreen_sampleData(initialPage = 1)

    private fun screenshotTest_LibraryScreen_sampleData(initialPage: Int) = screenshotTest(
        uiState = LibraryUiState(
            todosUiState = TodosUiState(
                todos = SampleData.SAMPLE_TODOS.take(itemsLimit),
                filteringUiInfo = TodosFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_todos,
                ),
            ),
            notesUiState = NotesUiState(
                notes = SampleData.SAMPLE_NOTES.take(itemsLimit),
                filteringUiInfo = NotesFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_notes,
                ),
            ),
            initialPage = initialPage,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_LibraryScreen_emptyState() = screenshotTest(
        uiState = LibraryUiState(
            todosUiState = TodosUiState(),
            notesUiState = NotesUiState(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_LibraryScreen_loadingState() = screenshotTest(
        uiState = LibraryUiState(
            todosUiState = TodosUiState(),
            notesUiState = NotesUiState(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_LibraryScreen_errorState() = screenshotTest(
        uiState = LibraryUiState(
            todosUiState = TodosUiState(),
            notesUiState = NotesUiState(),
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

                    onTodoClick = {},
                    onNoteClick = {},

                    navigator = FakeNavigator(),

                    modifier = Modifier,
                )
            }
        }

        composeTestRule.waitForIdle()

        screenshot()
    }

}