package com.ivzb.urgat.di

import com.ivzb.urgat.domain.inAppUpdate.CheckForUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.CompleteUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.DefaultCheckForUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.DefaultCompleteUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.DefaultObserveInstallStateUseCase
import com.ivzb.urgat.domain.inAppUpdate.DefaultStartFlexibleUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.ObserveInstallStateUseCase
import com.ivzb.urgat.domain.inAppUpdate.StartFlexibleUpdateUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface InAppUpdateUseCaseModule {

    @Singleton
    @Binds
    fun bindsCheckForUpdateUseCase(useCase: DefaultCheckForUpdateUseCase): CheckForUpdateUseCase

    @Singleton
    @Binds
    fun bindsObserveInstallStateUseCase(useCase: DefaultObserveInstallStateUseCase): ObserveInstallStateUseCase

    @Singleton
    @Binds
    fun bindsStartFlexibleUpdateUseCase(useCase: DefaultStartFlexibleUpdateUseCase): StartFlexibleUpdateUseCase

    @Singleton
    @Binds
    fun bindsCompleteUpdateUseCase(useCase: DefaultCompleteUpdateUseCase): CompleteUpdateUseCase
}

