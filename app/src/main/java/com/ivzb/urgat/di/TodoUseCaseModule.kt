package com.ivzb.urgat.di

import com.ivzb.urgat.data.todo.useCase.ActivateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.ClearCompletedTodosUseCase
import com.ivzb.urgat.data.todo.useCase.CompleteTodoUseCase
import com.ivzb.urgat.data.todo.useCase.CreateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.DefaultActivateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.DefaultClearCompletedTodosUseCase
import com.ivzb.urgat.data.todo.useCase.DefaultCompleteTodoUseCase
import com.ivzb.urgat.data.todo.useCase.DefaultCreateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.DefaultGetTodoUseCase
import com.ivzb.urgat.data.todo.useCase.DefaultObserveTodosUseCase
import com.ivzb.urgat.data.todo.useCase.DefaultUpdateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.GetTodoUseCase
import com.ivzb.urgat.data.todo.useCase.ObserveTodosUseCase
import com.ivzb.urgat.data.todo.useCase.UpdateTodoUseCase
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

}
