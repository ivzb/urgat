package com.ivzb.urgat.ui.investments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class InvestmentsFilterType: Parcelable {
    ALL_INVESTMENTS,
    ACTIVE_INVESTMENTS,
    COMPLETED_INVESTMENTS,
}