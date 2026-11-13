package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditInvestment.AddEditInvestmentRoute
import com.ivzb.urgat.ui.investments.InvestmentsRoute

fun UrgatNavActions.navigateToInvestments() {
    navigator.navigate(InvestmentsRoute)
}

fun UrgatNavActions.navigateToAddInvestment() {
    navigator.navigate(
        AddEditInvestmentRoute(
            id = null,
            titleResId = R.string.add_investment
        )
    )
}

fun UrgatNavActions.navigateToEditInvestment(investmentId: String) {
    navigator.navigate(
        AddEditInvestmentRoute(
            id = investmentId,
            titleResId = R.string.edit_investment
        )
    )
}
