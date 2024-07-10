package com.odesa.musicMatters.core.data.di

import android.content.Context
import com.odesa.musicMatters.core.data.database.MusicMattersDatabase
import com.odesa.musicMatters.core.data.playlists.impl.LocalPlaylistStore
import com.odesa.musicMatters.core.data.preferences.impl.PreferenceStoreImpl
import com.odesa.musicMatters.core.data.repository.PlaylistRepositoryImpl
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepositoryImpl
import com.odesa.musicMatters.core.data.search.impl.SearchHistoryRepositoryImpl
import com.odesa.musicMatters.core.data.search.impl.SearchHistoryStoreImpl
import com.odesa.musicMatters.core.data.settings.impl.SettingsRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import java.io.File
import java.io.IOException

class DataDiModule private constructor ( context: Context, dispatcher: CoroutineDispatcher ) {

    private val scope = CoroutineScope( dispatcher )
    private val preferenceStore = PreferenceStoreImpl( context )
    private val database = MusicMattersDatabase.getDatabase(
        context = context,
        scope = scope
    )
    private val playlistStore = LocalPlaylistStore(
        playlistDao = database.playlistDao(),
        playlistEntryDao = database.playlistEntryDao(),
        songPlayCountEntryDao = database.songPlayCountEntryDao(),
        currentTimeInMillis = { System.currentTimeMillis() }
    )
    private val searchHistoryStore = SearchHistoryStoreImpl(
        searchHistoryFile = retrieveSearchHistoryFileFromAppExternalCache( context )
    )
    val settingsRepository = SettingsRepositoryImpl.getInstance( preferenceStore )
    val playlistRepository = PlaylistRepositoryImpl.getInstance(
        playlistStore = playlistStore,
        coroutineScope = scope
    )
    val searchHistoryRepository = SearchHistoryRepositoryImpl( searchHistoryStore )

    private val songAdditionalMetadataDao = database.songAdditionalMetadataDao()
    val songsAdditionalMetadataRepository = SongsAdditionalMetadataRepositoryImpl(
        songAdditionalMetadataDao = songAdditionalMetadataDao
    )


    companion object {

        @Volatile
        private var INSTANCE: DataDiModule? = null

        fun getInstance( context: Context, dispatcher: CoroutineDispatcher ): DataDiModule {
            return INSTANCE ?: synchronized( this ) {
                DataDiModule(
                    context = context,
                    dispatcher = dispatcher
                ).also { INSTANCE = it }
            }
        }

        private fun retrieveSearchHistoryFileFromAppExternalCache( context: Context ): File {
            val file = File( context.dataDir.absolutePath, SEARCH_HISTORY_FILE_NAME )
            if ( !file.exists() ) {
                try {
                    file.createNewFile()
                } catch ( exception: IOException) {
                    Timber.tag( TAG ).d( "ERROR WHILE CREATE FILE: ${file.absolutePath}" )
                }
            }
            return file
        }
    }
}

private const val SEARCH_HISTORY_FILE_NAME = "search-history.json"
private const val TAG = "DATA-DI-MODULE-TAG"