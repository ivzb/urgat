package com.ivzb.urgat.util

import javax.inject.Inject

interface TimeProvider {

    fun currentTimeMillis(): Long

}

class DefaultTimeProvider @Inject constructor() : TimeProvider {

    override fun currentTimeMillis(): Long = System.currentTimeMillis()

}

