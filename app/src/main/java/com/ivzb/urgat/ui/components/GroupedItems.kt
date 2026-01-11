package com.ivzb.urgat.ui.components

data class GroupedItems<Group, Total, Item>(
    val group: Group,
    val total: Total,
    val items: List<Item>
)