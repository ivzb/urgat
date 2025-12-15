package com.ivzb.urgat.navigation

import androidx.navigation3.runtime.NavKey
import kotlin.collections.last
import kotlin.collections.removeLastOrNull

interface Navigator {

    fun navigate(route: NavKey)

    fun goBack()

}

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class UrgatNavigator(val state: NavigationState): Navigator {

    override fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            // This is a top level route, just switch to it.
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    override fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute] ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}

class FakeNavigator(): Navigator {

    override fun navigate(route: NavKey) = Unit

    override fun goBack() = Unit

}