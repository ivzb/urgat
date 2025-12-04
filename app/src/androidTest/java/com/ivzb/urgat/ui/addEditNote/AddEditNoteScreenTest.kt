package com.ivzb.urgat.ui.addEditNote

import androidx.compose.ui.Modifier
import androidx.test.espresso.Espresso
import com.ivzb.urgat.R
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.addEditNote.AddEditNoteScreen
import com.ivzb.urgat.ui.addEditNote.AddEditNoteUiState
import com.ivzb.urgat.ui.base.ScreenshotTest
import com.ivzb.urgat.ui.theme.UrgatTheme
import org.junit.Test

class AddEditNoteScreenTest: ScreenshotTest() {

    @Test
    fun screenshotTest_AddEditNoteScreen_addNote() = screenshotTest(
        uiState = AddEditNoteUiState(
            screenTitleResId = R.string.add_note,
        ),
    )

    @Test
    fun screenshotTest_AddEditNoteScreen_editNote() = screenshotTest(
        uiState = SampleData.SAMPLE_NOTES.first().let { note ->
            AddEditNoteUiState(
                screenTitleResId = R.string.edit_note,
                content = note.content,
            )
        },
    )

    @Test
    fun screenshotTest_AddEditNoteScreen_loading() = screenshotTest(
        uiState = AddEditNoteUiState(
            screenTitleResId = R.string.edit_note,
            isLoading = true,
        ),
    )

    private fun screenshotTest(uiState: AddEditNoteUiState) {
        composeTestRule.setContent {
            UrgatTheme {
                AddEditNoteScreen(
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