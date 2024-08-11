package com.odesa.musicMatters.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.odesa.musicMatters.core.data.database.dao.PlaylistDao
import com.odesa.musicMatters.core.data.database.dao.PlaylistEntryDao
import com.odesa.musicMatters.core.data.database.dao.SongAdditionalMetadataDao
import com.odesa.musicMatters.core.data.database.dao.SongPlayCountEntryDao
import com.odesa.musicMatters.core.data.database.model.Playlist
import com.odesa.musicMatters.core.data.database.model.PlaylistEntry
import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.data.database.model.SongPlayCountEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(
    entities = [
        Playlist::class,
        PlaylistEntry::class,
        SongPlayCountEntry::class,
        SongAdditionalMetadata::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicMattersDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistEntryDao(): PlaylistEntryDao
    abstract fun songPlayCountEntryDao(): SongPlayCountEntryDao
    abstract fun songAdditionalMetadataDao(): SongAdditionalMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: MusicMattersDatabase? = null

        fun getDatabase( context: Context, scope: CoroutineScope ): MusicMattersDatabase {
            return INSTANCE ?: synchronized( this ) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    MusicMattersDatabase::class.java,
                    "music-matters-database-4"
                ).build()
                scope.launch { addInitialPlaylists( instance.playlistDao() ) }
                INSTANCE = instance
                instance
            }
        }

        private suspend fun addInitialPlaylists( playlistDao: PlaylistDao ) {
            Timber.tag( TAG ).d( "ADDING INITIAL PLAYLISTS USING: $playlistDao" )
            addFavoritesPlaylist( playlistDao )
            addRecentlyPlayedSongsPlaylist( playlistDao )
            addCurrentPlayingQueuePlaylist( playlistDao )
        }

        private suspend fun addFavoritesPlaylist( dao: PlaylistDao ) {
            addPlaylist( dao, FAVORITES_PLAYLIST_ID, FAVORITES_PLAYLIST_TITLE )
        }

        private suspend fun addRecentlyPlayedSongsPlaylist( dao: PlaylistDao ) {
            addPlaylist( dao, RECENTLY_PLAYED_SONGS_PLAYLIST_ID, RECENTLY_PLAYED_SONGS_PLAYLIST_TITLE )
        }

        private suspend fun addCurrentPlayingQueuePlaylist( dao: PlaylistDao ) {
            addPlaylist( dao, CURRENT_PLAYING_QUEUE_PLAYLIST_ID, "" )
        }

        private suspend fun addPlaylist( dao: PlaylistDao, id: String, title: String ) {
            dao.apply {
                if ( !dao.playlistExists( id ) )
                    insert(
                        Playlist(
                            id = id,
                            title = title,
                        )
                    )
            }
        }
    }
}


const val FAVORITES_PLAYLIST_ID = "--MUSIC-MATTERS-FAVORITES-PLAYLIST-ID--"
internal const val FAVORITES_PLAYLIST_TITLE = "Favorites"

const val RECENTLY_PLAYED_SONGS_PLAYLIST_ID = "--MUSIC-MATTERS-RECENTLY-PLAYED-SONGS-PLAYLIST-ID"
internal const val RECENTLY_PLAYED_SONGS_PLAYLIST_TITLE = "Recently Played Songs"

const val CURRENT_PLAYING_QUEUE_PLAYLIST_ID = "--MUSIC-MATTERS-CURRENT_PLAYING_QUEUE-PLAYLIST-ID--"
private const val TAG = "--MUSIC-MATTERS-DATABASE-TAG--"
