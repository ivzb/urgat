package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.savings.SavingsSortMenuItem
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface ObserveSavingsSortingUseCase {

    operator fun invoke(): Flow<Async<List<SavingsSortMenuItem>>>

}

class DefaultObserveSavingsSortingUseCase @Inject constructor() : ObserveSavingsSortingUseCase {

    override fun invoke(): Flow<Async<List<SavingsSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = SavingsSortType.BY_DATE, labelResId = R.string.sort_by_date),
                SortMenuItem(type = SavingsSortType.BY_MIN, labelResId = R.string.sort_by_min),
                SortMenuItem(type = SavingsSortType.BY_MAX, labelResId = R.string.sort_by_max),
            )
        )
    )

}
