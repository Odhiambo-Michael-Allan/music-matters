package com.squad.musicmatters.core.media.di

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.media.connection.Connectable
import com.squad.musicmatters.core.media.connection.MediaBrowserAdapter
import com.squad.musicmatters.core.media.media.MusicService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@OptIn( UnstableApi::class )
@Module
@InstallIn( SingletonComponent::class )
object MediaDiModuleProviders {

    @OptIn( UnstableApi::class )
    @Provides
    fun providesServiceConnector(
        @ApplicationContext context: Context,
    ): Connectable = MediaBrowserAdapter(
        context = context,
        serviceComponentName = ComponentName(context, MusicService::class.java )
    )

}