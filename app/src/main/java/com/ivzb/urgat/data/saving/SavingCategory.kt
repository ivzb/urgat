package com.ivzb.urgat.data.saving

data class SavingCategory(
    val id: String,
    val name: String,
    val totals: List<SavingTotal>,

    val savings: List<Saving>,
)