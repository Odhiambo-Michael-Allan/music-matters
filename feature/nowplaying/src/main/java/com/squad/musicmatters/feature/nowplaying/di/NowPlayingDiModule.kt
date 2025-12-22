package com.squad.musicmatters.feature.nowplaying.di

import com.squad.musicmatters.feature.nowplaying.PlaybackPositionUpdater
import com.squad.musicmatters.feature.nowplaying.PlaybackPositionUpdaterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class )
abstract class NowPlayingDiModule {

    @Binds
    abstract fun bindsPlaybackPositionUpdater(
        updater: PlaybackPositionUpdaterImpl
    ): PlaybackPositionUpdater

}