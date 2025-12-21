package com.ivzb.urgat.navigation.drawer

import androidx.annotation.StringRes

data class UrgatNavigationDrawerSection(
    @StringRes
    val titleResId: Int,
    val items: List<UrgatNavigationDrawerItem>,
)