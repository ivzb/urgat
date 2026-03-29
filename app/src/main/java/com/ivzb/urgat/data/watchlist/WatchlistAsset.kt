package com.ivzb.urgat.data.watchlist

data class WatchlistAsset(
    val id: String,
    val name: String,
    val isDeleted: Boolean,
    val createdAt: Long
) {

    fun formatCsv(): String =
        "\"$id\", \"$name\", $createdAt"

    companion object {

        fun csvHeader(): String =
            "\"id\", \"name\", createdAt"
    }
}