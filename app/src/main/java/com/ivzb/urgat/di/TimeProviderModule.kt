package com.ivzb.urgat.di

import com.ivzb.urgat.util.DefaultTimeProvider
import com.ivzb.urgat.util.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeProviderModule {

    @Binds
    abstract fun bindTimeProvider(impl: DefaultTimeProvider): TimeProvider

}

