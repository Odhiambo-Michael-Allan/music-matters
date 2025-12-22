package com.squad.musicmatters.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.common.di.ApplicationScope
import com.squad.musicmatters.core.datastore.MusicMattersPreferencesDataSource
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.datastore.UserPreferences
import com.squad.musicmatters.core.datastore.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class )
internal object DataStoreModule {

    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher( MusicMattersDispatchers.IO ) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope( scope.coroutineContext + ioDispatcher )
        ) {
            context.dataStoreFile( "music_matters_user_preferences.pb" )
        }

    @Provides
    @Singleton
    internal fun providesPreferencesDataSource(
        dataStore: DataStore<UserPreferences>
    ): PreferencesDataSource =
        MusicMattersPreferencesDataSource( userPreferencesDataStore = dataStore )
}