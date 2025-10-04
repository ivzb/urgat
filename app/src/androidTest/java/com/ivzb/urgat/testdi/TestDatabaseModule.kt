package com.ivzb.urgat.testdi

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.di.RepositoryModule
import com.ivzb.urgat.data.source.di.FakeExpensesRepository

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
interface FakeDataModule {

    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeExpensesRepository
    ): ExpensesRepository
}
