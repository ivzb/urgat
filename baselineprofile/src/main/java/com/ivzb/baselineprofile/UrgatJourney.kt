package com.ivzb.baselineprofile

import android.os.Build
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

private const val UI_TIMEOUT_MS = 10_000L

private const val LOADING = "loading"

private const val TAB_LIBRARY = "nav_library"
private const val TAB_FINANCE = "nav_finance"
private const val TAB_CAR = "nav_car"
private const val TAB_WELLBEING = "nav_wellbeing"

val targetAppId: String
    get() = InstrumentationRegistry.getArguments().getString("targetAppId")
        ?: throw IllegalStateException("targetAppId not passed as instrumentation runner arg")

fun MacrobenchmarkScope.grantNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return
    }

    device.executeShellCommand("pm grant $targetAppId android.permission.POST_NOTIFICATIONS")
}

fun MacrobenchmarkScope.happyPathJourney() {
    startActivityAndWait()

    awaitTab(TAB_LIBRARY)
    awaitLoaded()

    openTab(TAB_FINANCE)
    openTab(TAB_CAR)
    openTab(TAB_WELLBEING)
}

private fun MacrobenchmarkScope.openTab(tab: String) {
    awaitTab(tab).click()

    if (!device.wait(Until.hasObject(By.res(tab).selected(true)), UI_TIMEOUT_MS)) {
        throw AssertionError("Navigation item \"$tab\" not selected after ${UI_TIMEOUT_MS}ms")
    }

    awaitLoaded()
}

private fun MacrobenchmarkScope.awaitTab(tab: String): UiObject2 =
    device.wait(Until.findObject(By.res(tab)), UI_TIMEOUT_MS)
        ?: throw AssertionError("Navigation item \"$tab\" not found after ${UI_TIMEOUT_MS}ms")

private fun MacrobenchmarkScope.awaitLoaded() {
    if (!device.wait(Until.gone(By.res(LOADING)), UI_TIMEOUT_MS)) {
        throw AssertionError("Screen still loading after ${UI_TIMEOUT_MS}ms")
    }
}
