package com.ivzb.urgat.util

import com.ivzb.urgat.BuildConfig

object Tracker {

    fun error(exception: Throwable) {
        if (BuildConfig.DEBUG) {
            exception.printStackTrace()
        }
    }

}