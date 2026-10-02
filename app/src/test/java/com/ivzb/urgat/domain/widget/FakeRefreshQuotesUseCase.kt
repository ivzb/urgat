package com.ivzb.urgat.domain.widget

class FakeRefreshQuotesUseCase : RefreshQuotesUseCase {

    /** What the refresh should report back. */
    var succeeds = true

    /** Every set of types it was asked to refresh, in order. */
    val calls = mutableListOf<Set<WidgetType>>()

    var lastTimeoutMillis: Long? = null
        private set

    override suspend fun invoke(types: Set<WidgetType>, timeoutMillis: Long): Boolean {
        calls += types
        lastTimeoutMillis = timeoutMillis
        return succeeds
    }

}

