package com.ivzb.urgat.ui.about

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AboutViewModelTest {

    private lateinit var viewModel: AboutViewModel

    @Before
    fun setup() {
        viewModel = AboutViewModel()
    }

    @Test
    fun `uiState emits AboutUiState`() {
        assertEquals(AboutUiState, viewModel.uiState.value)
    }

    @Test
    fun `uiState is stable across reads`() {
        val first = viewModel.uiState.value
        val second = viewModel.uiState.value

        assertSame(first, second)
    }

}

