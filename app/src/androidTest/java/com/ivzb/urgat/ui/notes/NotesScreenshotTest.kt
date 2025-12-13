package com.ivzb.urgat.ui.notes

import androidx.compose.ui.Modifier
import com.ivzb.urgat.annotations.Screenshot
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.suites.PlayStoreScreenshotTest
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test
import org.junit.experimental.categories.Category

@Screenshot
class NotesScreenshotTest: ScreenshotTest() {

    @Test
    @Category(PlayStoreScreenshotTest::class)
    fun screenshotTest_NotesScreen_sampleData() = screenshotTest(
        uiState = NotesUiState(
            items = SampleData.SAMPLE_NOTES,
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_NotesScreen_emptyState() = screenshotTest(
        uiState = NotesUiState(
            items = listOf(),
            isLoading = false,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_NotesScreen_loadingState() = screenshotTest(
        uiState = NotesUiState(
            items = listOf(),
            isLoading = true,
            hasError = false,
        ),
    )

    @Test
    fun screenshotTest_NotesScreen_errorState() = screenshotTest(
        uiState = NotesUiState(
            items = listOf(),
            isLoading = false,
            hasError = true,
        ),
    )

    private fun screenshotTest(uiState: NotesUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                NotesScreen(
                    uiState = uiState,
                    onIntent = {},
                    onAddNote = {},
                    onNoteClick = {},
                    onBack = {},
                    modifier = Modifier
                )
            }
        }

        screenshot()
    }

}