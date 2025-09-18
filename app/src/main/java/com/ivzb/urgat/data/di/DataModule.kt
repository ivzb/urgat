package com.ivzb.urgat.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.ivzb.urgat.data.ExpensesRepository
import com.ivzb.urgat.data.DefaultExpensesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsExpensesRepository(
        expensesRepository: DefaultExpensesRepository
    ): ExpensesRepository
}

class FakeExpensesRepository @Inject constructor() : ExpensesRepository {
    override val expensess: Flow<List<String>> = flowOf(fakeExpensess)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}

val fakeExpensess = listOf("One", "Two", "Three")
