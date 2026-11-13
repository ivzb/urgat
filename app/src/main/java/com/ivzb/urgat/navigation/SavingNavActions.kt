package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditSaving.AddEditSavingRoute
import com.ivzb.urgat.ui.savings.SavingsRoute

fun UrgatNavActions.navigateToSavings() {
    navigator.navigate(SavingsRoute)
}

fun UrgatNavActions.navigateToAddSaving() {
    navigator.navigate(
        AddEditSavingRoute(
            id = null,
            titleResId = R.string.add_saving
        )
    )
}

fun UrgatNavActions.navigateToEditSaving(savingId: String) {
    navigator.navigate(
        AddEditSavingRoute(
            id = savingId,
            titleResId = R.string.edit_saving
        )
    )
}
