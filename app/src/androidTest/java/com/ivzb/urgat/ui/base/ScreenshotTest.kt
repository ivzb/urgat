package com.ivzb.urgat.ui.base

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.canTakeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName

abstract class ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val nameRule = TestName()

    @Before
    fun disableTransitions() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(10_000)
    }

    protected fun screenshot() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(0) // forces deterministic draw

        if (canTakeScreenshot()) {
            /*
             * Capture a screenshot of the Compose root instead of the full screen.
             * This prevents false diffs caused by changes in the status bar (e.g., clock updates).
             * We only want diffs when something in the app actually changes.
             */
            composeTestRule.onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
        } else {
            throw IllegalStateException("Screenshot cannot be taken.")
        }
    }
}