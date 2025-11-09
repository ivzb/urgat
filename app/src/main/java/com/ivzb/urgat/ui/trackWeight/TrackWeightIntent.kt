package com.ivzb.urgat.ui.trackWeight

sealed class TrackWeightIntent {
    data class UpdateValue(val value: String): TrackWeightIntent()
    object SaveWeight: TrackWeightIntent()
    object UserMessageShown: TrackWeightIntent()
}