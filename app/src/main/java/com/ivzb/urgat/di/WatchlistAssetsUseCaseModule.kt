package com.ivzb.urgat.di

import com.ivzb.urgat.domain.watchlist.CreateWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.DefaultCreateWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.DefaultDeleteWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.DefaultGetWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.DefaultUpdateWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.DeleteWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.GetWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.UpdateWatchlistAssetUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface WatchlistAssetsUseCaseModule {

    @Binds
    fun bindsGetWatchlistAssetUseCase(useCase: DefaultGetWatchlistAssetUseCase): GetWatchlistAssetUseCase

    @Singleton
    @Binds
    fun bindsCreateWatchlistAssetUseCase(useCase: DefaultCreateWatchlistAssetUseCase): CreateWatchlistAssetUseCase

    @Singleton
    @Binds
    fun bindsUpdateWatchlistAssetUseCase(useCase: DefaultUpdateWatchlistAssetUseCase): UpdateWatchlistAssetUseCase

    @Singleton
    @Binds
    fun bindsDeleteWatchlistAssetUseCase(useCase: DefaultDeleteWatchlistAssetUseCase): DeleteWatchlistAssetUseCase

}

