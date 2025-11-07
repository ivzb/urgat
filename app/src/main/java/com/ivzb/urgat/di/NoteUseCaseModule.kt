package com.ivzb.urgat.di

import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.note.DefaultCreateNoteUseCase
import com.ivzb.urgat.domain.note.DefaultGetNoteUseCase
import com.ivzb.urgat.domain.note.DefaultObserveNotesUseCase
import com.ivzb.urgat.domain.note.DefaultUpdateNoteUseCase
import com.ivzb.urgat.domain.note.GetNoteUseCase
import com.ivzb.urgat.domain.note.ObserveNotesUseCase
import com.ivzb.urgat.domain.note.UpdateNoteUseCase
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
