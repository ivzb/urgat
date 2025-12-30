package com.ivzb.urgat.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.navigation3.ui.NavDisplay
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.splash.SplashRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UrgatNavGraph(
    modifier: Modifier = Modifier,
) {
    val topLevelRoutes = BOTTOM_NAVIGATION_SCREENS.map { it.route } + SplashRoute
    val navigationState = rememberNavigationState(
        startRoute = SplashRoute,
        topLevelRoutes = topLevelRoutes.toSet()
    )
    val navigator = remember { UrgatNavigator(navigationState) }
    val navActions = remember(navigator) { UrgatNavActions(navigator) }
    val entryProvider = remember(navigator, navActions) {
        urgatEntryProvider(navigator, navActions)
    }

    NavDisplay(
        onBack = { navigator.goBack() },
        entries = navigationState.toEntries(entryProvider),
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        popTransitionSpec = { fadeIn() togetherWith fadeOut() },
        predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )

    DoubleBackToExitApp(navigationState, navigator)
}

@Composable
private fun DoubleBackToExitApp(
    navigationState: NavigationState,
    navigator: Navigator,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    var backPressedOnce by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler {
        if (!navigationState.isRoot) {
            navigator.goBack()
            return@BackHandler
        }

        if (backPressedOnce) {
            (context as? Activity)?.finish()
            return@BackHandler
        }

        backPressedOnce = true
        Toast.makeText(context, resources.getString(R.string.press_back_once_more_to_exit), Toast.LENGTH_SHORT).show()

        scope.launch {
            delay(2_000)
            backPressedOnce = false
        }
    }
}
