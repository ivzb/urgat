package com.ivzb.urgat.util

import com.ivzb.urgat.BuildConfig

object Assert {

    fun fail(message: String) {
        if (BuildConfig.DEBUG) {
            throw IllegalStateException(message)
        }
    }

}