package com.ivzb.urgat.ui

data class GroupedItems<T, K>(
    val group: T,
    val items: List<K>
)