package com.ivzb.urgat.ui.wellbeing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.HeaderItem

typealias WellbeingHeaderItem = HeaderItem<WellbeingHeaderItemId>

val WELLBEING_HEADER_ITEMS = listOf(
    HeaderItem(
        WellbeingHeaderItemId.Weight,
        Icons.Filled.FitnessCenter,
        R.string.track_weight
    ),
)