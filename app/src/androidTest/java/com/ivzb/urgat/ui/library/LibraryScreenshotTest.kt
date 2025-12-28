package com.ivzb.urgat.ui.library

import androidx.compose.ui.Modifier
import com.ivzb.urgat.R
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.FakeNavigator
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class LibraryScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_LibraryScreen_sampleData() = screenshotTest(
        uiState = LibraryUiState(
            todos = SampleData.SAMPLE_TODOS.take(itemsLimit),
            todosFilteringUiInfo = TodosFilteringUiInfo(
                currentFilteringLabel = R.string.recent_todos,
            ),
            notes = SampleData.SAMPLE_NOTES.take(itemsLimit),
            notesFilteringUiInfo = NotesFilteringUiInfo(
                currentFilteringLabel = R.string.recent_notes,
            ),
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

                    navigator = FakeNavigator(),

                    modifier = Modifier,
                )
            }
        }

        composeTestRule.waitForIdle()

        screenshot()
    }

}