package com.ivzb.urgat.util

sealed class Async<out T> {
    object Loading : Async<Nothing>()

    data class Error(val errorMessage: Int, val throwable: Throwable) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}
