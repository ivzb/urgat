package com.ivzb.urgat.di

import com.ivzb.urgat.data.expense.DefaultExpensesRepository
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.investment.DefaultInvestmentsRepository
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.note.DefaultNotesRepository
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.data.todo.DefaultTodoRepository
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.data.weight.DefaultWeightsRepository
import com.ivzb.urgat.data.weight.WeightsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindTodoRepository(todoRepository: DefaultTodoRepository): TodoRepository

    @Singleton
    @Binds
    fun bindsExpensesRepository(expensesRepository: DefaultExpensesRepository): ExpensesRepository

    @Singleton
    @Binds
    fun bindsNotesRepository(notesRepository: DefaultNotesRepository): NotesRepository

    @Singleton
    @Binds
    fun bindsWeightsRepository(weightsRepository: DefaultWeightsRepository): WeightsRepository

    @Singleton
    @Binds
    fun bindsInvestmentsRepository(invsetmentsRepository: DefaultInvestmentsRepository): InvestmentsRepository

}
