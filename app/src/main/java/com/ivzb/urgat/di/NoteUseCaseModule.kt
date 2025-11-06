package com.ivzb.urgat.di

import com.ivzb.urgat.data.note.useCase.CreateNoteUseCase
import com.ivzb.urgat.data.note.useCase.DefaultCreateNoteUseCase
import com.ivzb.urgat.data.note.useCase.DefaultGetNoteUseCase
import com.ivzb.urgat.data.note.useCase.DefaultObserveNotesUseCase
import com.ivzb.urgat.data.note.useCase.DefaultUpdateNoteUseCase
import com.ivzb.urgat.data.note.useCase.GetNoteUseCase
import com.ivzb.urgat.data.note.useCase.ObserveNotesUseCase
import com.ivzb.urgat.data.note.useCase.UpdateNoteUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NoteUseCaseModule {

    @Singleton
    @Binds
    fun bindsGetNoteUseCase(useCase: DefaultGetNoteUseCase): GetNoteUseCase

    @Singleton
    @Binds
    fun bindsObserveNotesUseCase(useCase: DefaultObserveNotesUseCase): ObserveNotesUseCase

    @Singleton
    @Binds
    fun bindsUpdateNoteUseCase(useCase: DefaultUpdateNoteUseCase): UpdateNoteUseCase

    @Singleton
    @Binds
    fun bindsCreateNoteUseCase(useCase: DefaultCreateNoteUseCase): CreateNoteUseCase

}
