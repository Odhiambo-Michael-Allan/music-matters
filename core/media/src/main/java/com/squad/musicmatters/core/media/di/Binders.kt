package com.squad.musicmatters.core.media.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.media.connection.DefaultSongToMediaItemConverter
import com.squad.musicmatters.core.media.connection.MusicMattersPlayerController
import com.squad.musicmatters.core.media.connection.MusicMattersPlayerControllerImpl
import com.squad.musicmatters.core.media.connection.SongToMediaItemConverter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
@OptIn( UnstableApi::class )
abstract class MediaDiModuleBinders {

    @Binds
    @Singleton // THIS IS CRUCIAL!!
    abstract fun bindsMusicServiceConnection(
        connection: MusicMattersPlayerControllerImpl
    ): MusicMattersPlayerController

    @Binds
    abstract fun bindsSongToMediaItemConverter(
        converter: DefaultSongToMediaItemConverter
    ): SongToMediaItemConverter

}