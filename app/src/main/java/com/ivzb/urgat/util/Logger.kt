package com.ivzb.urgat.util

import android.util.Log
import com.ivzb.urgat.BuildConfig
import javax.inject.Inject

interface Logger {

    fun debug(message: String)

    fun error(message: String, throwable: Throwable? = null)

}

class DebugLogger @Inject constructor() : Logger {

    override fun debug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    override fun error(message: String, throwable: Throwable?) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, throwable)
        }
    }

    companion object {
        private const val TAG = "Urgat"
    }

}


