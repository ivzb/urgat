package com.ivzb.urgat.data.trade

import com.ivzb.urgat.data.base.BaseRepository
import kotlinx.coroutines.flow.StateFlow

interface TradesRepository: BaseRepository<Trade> {

    val isRefreshingFlow: StateFlow<Boolean>

}

