package com.ivzb.urgat.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.ivzb.urgat.data.local.database.AppDatabase
import com.ivzb.urgat.data.local.database.ExpensesDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideExpensesDao(appDatabase: AppDatabase): ExpensesDao {
        return appDatabase.expensesDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Expenses"
        ).build()
    }
}
