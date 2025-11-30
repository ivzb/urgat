package com.ivzb.urgat.ui.trackWeight

sealed class TrackWeightIntent {
    data class UpdateValue(val value: String): TrackWeightIntent()
    data class UpdateDate(val date: Long): TrackWeightIntent()
    object SaveWeight: TrackWeightIntent()
    object UserMessageShown: TrackWeightIntent()
}