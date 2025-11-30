package com.ivzb.urgat.di

import android.content.Context
import androidx.room.Room
import com.ivzb.urgat.data.source.local.ALL_MIGRATIONS
import com.ivzb.urgat.data.source.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(appContext, AppDatabase::class.java, "Urgat")
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }
}
