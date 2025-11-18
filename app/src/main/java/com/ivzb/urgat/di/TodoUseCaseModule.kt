package com.ivzb.urgat.di

import com.ivzb.urgat.domain.todo.ActivateTodoUseCase
import com.ivzb.urgat.domain.todo.ClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.CompleteTodoUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import com.ivzb.urgat.domain.todo.DefaultActivateTodoUseCase
import com.ivzb.urgat.domain.todo.DefaultClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.DefaultCompleteTodoUseCase
import com.ivzb.urgat.domain.todo.DefaultCreateTodoUseCase
import com.ivzb.urgat.domain.todo.DefaultGetTodoUseCase
import com.ivzb.urgat.domain.todo.DefaultObserveTodosFilteringUseCase
import com.ivzb.urgat.domain.todo.DefaultObserveTodosUseCase
import com.ivzb.urgat.domain.todo.DefaultUpdateTodoUseCase
import com.ivzb.urgat.domain.todo.GetTodoUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosFilteringUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase
import com.ivzb.urgat.domain.todo.UpdateTodoUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TodoUseCaseModule {

    @Singleton
    @Binds
    fun bindsActivateTodoUseCase(useCase: DefaultActivateTodoUseCase): ActivateTodoUseCase

    @Singleton
    @Binds
    fun bindsClearCompletedTodosUseCase(useCase: DefaultClearCompletedTodosUseCase): ClearCompletedTodosUseCase

    @Singleton
    @Binds
    fun bindsCompleteTodoUseCase(useCase: DefaultCompleteTodoUseCase): CompleteTodoUseCase

    @Singleton
    @Binds
    fun bindsGetTodoUseCase(useCase: DefaultGetTodoUseCase): GetTodoUseCase

    @Singleton
    @Binds
    fun bindsObserveTodosUseCase(useCase: DefaultObserveTodosUseCase): ObserveTodosUseCase

    @Singleton
    @Binds
    fun bindsUpdateTodoUseCase(useCase: DefaultUpdateTodoUseCase): UpdateTodoUseCase

    @Singleton
    @Binds
    fun bindsCreateTodoUseCase(useCase: DefaultCreateTodoUseCase): CreateTodoUseCase

    @Singleton
    @Binds
    fun bindsObserveTodosFilteringUseCase(useCase: DefaultObserveTodosFilteringUseCase): ObserveTodosFilteringUseCase

}
