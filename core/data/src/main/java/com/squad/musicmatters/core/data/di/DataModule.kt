package com.squad.musicmatters.core.data.di

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.repository.CompositeRepository
import com.squad.musicmatters.core.data.repository.MostPlayedSongsRepository
import com.squad.musicmatters.core.data.repository.PlayHistoryRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.repository.impl.AlbumsRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.ArtistsRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.CompositeRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.MostPlayedSongsRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.PlayHistoryRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.PlaylistsRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.QueueRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.SongsMetadataRepositoryImpl
import com.squad.musicmatters.core.data.repository.impl.SongsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
abstract class DataModule {

    @Binds
    @Singleton
    internal abstract fun bindsCompositeRepository(
        compositeRepository: CompositeRepositoryImpl
    ): CompositeRepository

    @Binds
    @Singleton
    internal abstract fun bindsMostPlayedSongsRepository(
        mostPlayedSongsRepository: MostPlayedSongsRepositoryImpl
    ): MostPlayedSongsRepository

    @Binds
    @Singleton
    internal abstract fun bindsPlayHistoryRepository(
        playHistoryRepository: PlayHistoryRepositoryImpl
    ): PlayHistoryRepository

    @Binds
    @Singleton
    internal abstract fun bindsPlaylistRepository(
        repository: PlaylistsRepositoryImpl
    ): PlaylistsRepository

    @Binds
    @Singleton
    internal abstract fun bindsQueueRepository(
        repository: QueueRepositoryImpl
    ): QueueRepository

    @Binds
    @Singleton
    internal abstract fun bindsSongsAdditionalMetadataRepository(
        repository: SongsMetadataRepositoryImpl
    ): SongsMetadataRepository

    @Binds
    @Singleton
    internal abstract fun bindsSongsRepository(
        repository: SongsRepositoryImpl
    ): SongsRepository

    @Binds
    @Singleton
    internal abstract fun bindsAlbumsRepository(
        repository: AlbumsRepositoryImpl
    ): AlbumsRepository

    @Binds
    @Singleton
    internal abstract fun bindsArtistsRepository(
        repository: ArtistsRepositoryImpl
    ): ArtistsRepository

}