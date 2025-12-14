package com.ivzb.urgat.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

@Preview
@DarkModePreview
@LandscapePreview
@DarkModeLandscapePreview
@TabletPreview
@TabletDarkModePreview
@TabletLandscapePreview
@TabletDarkModeLandscapePreview
annotation class AllDevicesPreview

@Preview(
    name = "Phone Landscape",
    widthDp = 640,
    heightDp = 360
)
annotation class LandscapePreview

@Preview(
    name = "Tablet",
    widthDp = 800,
    heightDp = 1280
)
annotation class TabletPreview

@Preview(
    name = "Tablet Dark Mode",
    widthDp = 800,
    heightDp = 1280,
    uiMode = UI_MODE_NIGHT_YES,
)
annotation class TabletDarkModePreview

@Preview(
    name = "Tablet Landscape",
    widthDp = 1280,
    heightDp = 800
)
annotation class TabletLandscapePreview

@Preview(
    name = "Tablet Dark Mode Landscape",
    widthDp = 1280,
    heightDp = 800,
    uiMode = UI_MODE_NIGHT_YES,
)
annotation class TabletDarkModeLandscapePreview


@Preview(
    name = "Foldable - Unfolded",
    widthDp = 1080,
    heightDp = 1768
)
annotation class FoldablePreviewUnfolded

@Preview(
    name = "Foldable - Folded",
    widthDp = 540,
    heightDp = 1768
)
annotation class FoldablePreviewFolded

@Preview(
    name = "DarkMode",
    uiMode = UI_MODE_NIGHT_YES,
)
annotation class DarkModePreview

@Preview(
    name = "Phone Dark Mode Landscape",
    uiMode = UI_MODE_NIGHT_YES,
    widthDp = 640,
    heightDp = 360
)
annotation class DarkModeLandscapePreview
