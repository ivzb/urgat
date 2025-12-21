package com.ivzb.urgat.ui.sample

sealed class SampleIntent {
    data object AddSampleData: SampleIntent()
    data object UserMessageShown: SampleIntent()
}
