package com.ivzb.urgat.testdi

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import com.ivzb.urgat.data.ExpensesRepository
import com.ivzb.urgat.data.di.DataModule
import com.ivzb.urgat.data.di.FakeExpensesRepository

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeExpensesRepository
    ): ExpensesRepository
}
