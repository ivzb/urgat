package com.ivzb.urgat.ui.expenses

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for [ExpensesScreen].
 */
@RunWith(AndroidJUnit4::class)
class ExpensesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            ExpensesScreen(FAKE_DATA, onSave = {})
        }
    }

    @Test
    fun firstItem_exists() {
        composeTestRule.onNodeWithText(FAKE_DATA.first()).assertExists().performClick()
    }
}

private val FAKE_DATA = listOf("Compose", "Room", "Kotlin")
