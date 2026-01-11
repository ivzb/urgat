package com.ivzb.urgat.ui.components.uiState

import com.ivzb.urgat.ui.components.Page

interface PagerUiState: UiState {

    val pages: List<Page>
    val initialPage: Int

}