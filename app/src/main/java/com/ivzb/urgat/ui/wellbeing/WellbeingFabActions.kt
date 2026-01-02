package com.ivzb.urgat.ui.wellbeing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FabAction

typealias WellbeingFabAction = FabAction<WellbeingFabActionId>

val WELLBEING_FAB_ACTIONS = listOf(
    FabAction(
        id = WellbeingFabActionId.Weight,
        icon = Icons.Filled.FitnessCenter,
        labelResId = R.string.add_weight
    ),
)