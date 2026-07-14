package com.squad.musicmatters.core.data.di

import android.content.Context
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.common.di.IoScope
import com.squad.musicmatters.core.data.store.GenresStore
import com.squad.musicmatters.core.data.store.MetadataStore
import com.squad.musicmatters.core.data.store.SongsStore
import com.squad.musicmatters.core.data.store.impl.GenresStoreImpl
import com.squad.musicmatters.core.data.store.impl.MetadataStoreImpl
import com.squad.musicmatters.core.data.store.impl.SongsStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
internal object MediaStoreModule {

    @Provides
    @Singleton
    internal fun providesSongsStore(
        @ApplicationContext context: Context,
        @Dispatcher(MusicMattersDispatchers.IO ) ioDispatcher: CoroutineDispatcher,
        @IoScope ioScope: CoroutineScope
    ): SongsStore = SongsStoreImpl(
        context = context,
        ioDispatcher = ioDispatcher,
        ioScope = ioScope,
    )

    @Provides
    @Singleton
    internal fun providesGenresStore(
        @ApplicationContext context: Context,
        @Dispatcher(MusicMattersDispatchers.IO ) ioDispatcher: CoroutineDispatcher,
        @IoScope ioScope: CoroutineScope
    ): GenresStore = GenresStoreImpl(
        context = context,
        ioDispatcher = ioDispatcher,
        ioScope = ioScope,
    )

    @Provides
    @Singleton
    internal fun providesMetadataStore(
        @ApplicationContext context: Context
    ) : MetadataStore = MetadataStoreImpl( context = context )

}