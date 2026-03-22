package com.ivzb.urgat.util

class FakeLogger : Logger {

    override fun debug(message: String) = Unit

    override fun error(message: String, throwable: Throwable?) = Unit

}

