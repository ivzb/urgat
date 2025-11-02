package com.ivzb.urgat.ui.trackWeight

sealed class TrackWeightIntent {
    data class UpdateWeight(val value: String): TrackWeightIntent()
    object SaveWeight: TrackWeightIntent()
    object UserMessageShown: TrackWeightIntent()
}