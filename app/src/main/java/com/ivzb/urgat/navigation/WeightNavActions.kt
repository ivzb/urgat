package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditWeight.AddEditWeightRoute

fun UrgatNavActions.navigateToAddWeight() {
    navigator.navigate(
        AddEditWeightRoute(
            id = null,
            titleResId = R.string.add_weight,
        )
    )
}

fun UrgatNavActions.navigateToEditWeight(weightId: String) {
    navigator.navigate(
        AddEditWeightRoute(
            id = weightId,
            titleResId = R.string.edit_weight,
        )
    )
}
