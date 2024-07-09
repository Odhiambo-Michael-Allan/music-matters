package com.odesa.musicMatters.core.data.di

import android.content.Context
import com.odesa.musicMatters.core.data.database.MusicMattersDatabase
import com.odesa.musicMatters.core.data.playlists.impl.Clock
import com.odesa.musicMatters.core.data.playlists.impl.LocalPlaylistStore
import com.odesa.musicMatters.core.data.playlists.impl.PlaylistRepositoryImpl
import com.odesa.musicMatters.core.data.preferences.impl.PreferenceStoreImpl
import com.odesa.musicMatters.core.data.search.impl.SearchHistoryRepositoryImpl
import com.odesa.musicMatters.core.data.search.impl.SearchHistoryStoreImpl
import com.odesa.musicMatters.core.data.settings.impl.SettingsRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.Calendar

class DataDiModule private constructor ( context: Context, dispatcher: CoroutineDispatcher ) {

    private val preferenceStore = PreferenceStoreImpl( context )
    private val database = MusicMattersDatabase.getDatabase(
        context = context,
        dispatcher = dispatcher
    )
    private val playlistStore = LocalPlaylistStore(
        playlistDao = database.playlistDao(),
        playlistEntryDao = database.playlistEntryDao(),
        songPlayCountEntryDao = database.songPlayCountEntryDao(),
        clock = RealClock
    )
    private val searchHistoryStore = SearchHistoryStoreImpl(
        searchHistoryFile = retrieveFileFromAppExternalCache( SEARCH_HISTORY_FILE_NAME, context )
    )
    val settingsRepository = SettingsRepositoryImpl.getInstance( preferenceStore )
    val playlistRepository = PlaylistRepositoryImpl.getInstance(
        playlistStore = playlistStore,
        coroutineDispatcher = dispatcher
    )
    val searchHistoryRepository = SearchHistoryRepositoryImpl( searchHistoryStore )


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

        private fun retrieveFileFromAppExternalCache( fileName: String, context: Context ): File {
            val file = File( context.dataDir.absolutePath, fileName )
            if ( !file.exists() ) {
                Timber.tag( TAG ).d( "CREATING NEW FILE IN APP EXTERNAL CACHE: $fileName" )
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

private val RealClock = object : Clock {
    private val calendar = Calendar.getInstance()
    override val currentTimeInMillis: Long
        get() = calendar.timeInMillis

}

private const val SEARCH_HISTORY_FILE_NAME = "search-history.json"
private const val TAG = "DATA-DI-MODULE-TAG"