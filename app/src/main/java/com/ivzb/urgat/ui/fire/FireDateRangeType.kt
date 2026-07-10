package com.ivzb.urgat.ui.fire

import android.os.Parcelable
import androidx.annotation.StringRes
import com.ivzb.urgat.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FireDateRangeType(
    @StringRes val labelResId: Int,
): Parcelable {
    LAST_MONTH(R.string.fire_date_range_last_month),
    LAST_YEAR(R.string.fire_date_range_last_year),
    YTD(R.string.fire_date_range_ytd),
    CUSTOM(R.string.fire_date_range_custom),
}


