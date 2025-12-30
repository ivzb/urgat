package com.ivzb.urgat.ui.splash

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.ui.library.LibraryRoute

fun EntryProviderScope<NavKey>.splashEntry(navigator: Navigator) {
    entry<SplashRoute> {
        SplashScreen(
            viewModel = hiltViewModel(),
            onComplete = {
                navigator.navigate(LibraryRoute)
            },
        )
    }
}