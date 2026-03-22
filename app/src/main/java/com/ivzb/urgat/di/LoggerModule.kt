package com.ivzb.urgat.di

import com.ivzb.urgat.util.DebugLogger
import com.ivzb.urgat.util.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LoggerModule {

    @Singleton
    @Binds
    fun bindLogger(debugLogger: DebugLogger): Logger

}

