package com.ivzb.urgat.util

sealed class Async<out T> {
    object Loading: Async<Nothing>()

    object Error: Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}