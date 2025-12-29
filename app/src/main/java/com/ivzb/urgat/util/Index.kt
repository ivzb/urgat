package com.ivzb.urgat.util

fun Int.isFirst(): Boolean {
    return this == 0
}

fun Int.isLast(size: Int): Boolean {
    return this == size - 1
}