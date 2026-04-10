package com.ivzb.urgat.di

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.ivzb.urgat.data.inAppUpdate.DefaultInAppUpdateRepository
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InAppUpdateProviderModule {

    @Provides
    @Singleton
    fun provideAppUpdateManager(@ApplicationContext context: Context): AppUpdateManager {
        return AppUpdateManagerFactory.create(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface InAppUpdateBindsModule {

    @Singleton
    @Binds
    fun bindInAppUpdateRepository(
        repository: DefaultInAppUpdateRepository
    ): InAppUpdateRepository
}

