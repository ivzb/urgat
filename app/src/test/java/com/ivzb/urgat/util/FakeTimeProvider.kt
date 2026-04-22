package com.ivzb.urgat.util

class FakeTimeProvider(var currentTime: Long = 0L) : TimeProvider {

    override fun currentTimeMillis(): Long = currentTime

}

