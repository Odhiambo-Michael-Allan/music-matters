package com.squad.musicmatters.core.data.store.impl

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import androidx.annotation.WorkerThread
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.squad.musicmatters.core.data.store.MediaStoreListener
import com.squad.musicmatters.core.data.store.SongsStore
import com.squad.musicmatters.core.data.store.MusicMattersMediaStore
import com.squad.musicmatters.core.data.store.getLongFrom
import com.squad.musicmatters.core.data.store.getNullableStringFrom
import com.squad.musicmatters.core.data.utils.sortSongs
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Lyrics
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.annotation.concurrent.Immutable

class SongsStoreImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    ioScope: CoroutineScope,
) : MusicMattersMediaStore(
    context = context,
    ioScope = ioScope
), SongsStore {

    private var _cachedSongs = MutableSharedFlow<List<Song>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        ioScope.launch { _cachedSongs.tryEmit( fetchSongs() ) }
        registerListener(
            object : MediaStoreListener {
                override fun onMediaStoreChanged() {
                    ioScope.launch { _cachedSongs.tryEmit( fetchSongs() ) }
                }
            }
        )
    }

    override fun fetchSongsFlow(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean,
    ): Flow<List<Song>> = _cachedSongs.map {
        it.sortSongs(
            by = sortSongsBy ?: DefaultPreferences.SORT_SONGS_BY,
            reverse = sortSongsInReverse
        )
    }.flowOn( ioDispatcher )

    override suspend fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean,
    ): List<Song> = fetchSongs(
        sortOrder = sortSongsBy?.toMediaStoreSortFormat(),
        sortSongsInReverse = sortSongsInReverse,
        selection = "$IS_MUSIC = 1"
    )

    override suspend fun searchSongsMatching(
        query: String,
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean
    ): List<Song> = fetchSongs(
        sortOrder = sortSongsBy?.toMediaStoreSortFormat()
            ?: SortSongsBy.TITLE.toMediaStoreSortFormat(),
        selection = "$IS_MUSIC = 1 AND ${AudioColumns.TITLE} LIKE ?",
        selectionArgs = arrayOf( "%${query.trim()}%" )
    )

    override suspend fun searchSongsInAlbumMatching(
        query: String,
    ): List<Song> = fetchSongs(
        selection = AudioColumns.ALBUM + " LIKE ?",
        selectionArgs = arrayOf( "%$query%" ),
    )

    override suspend fun searchSongsByArtistMatching(
        query: String,
    ): List<Song> = fetchSongs(
        selection = AudioColumns.ARTIST + " LIKE ?",
        selectionArgs = arrayOf( "%$query%" )
    )

    private suspend fun fetchSongs(
        sortSongsInReverse: Boolean = false,
        selection: String,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null,
    ) = withContext( ioDispatcher )  {
        val songList = mutableListOf<Song>()
        try {
            Timber.tag( TAG ).d( "FETCHING SONGS.." )
            context.contentResolver.query(
                collectionUri,
                projection,
                selection,
                selectionArgs,
                sortOrder,
            )?.use {
                while ( it.moveToNext() ) {
                    runCatching {
                        buildSongUsing( it )
                    }.getOrNull()?.also { song -> songList.add( song ) }
                }
            }
            val sortedList = if ( sortSongsInReverse ) songList.reversed() else songList
            return@withContext sortedList
        } catch ( exception: Exception ) {
            exception.message?.let {
                Timber.tag( TAG )
                    .e( "ERROR WHILE FETCHING SONGS. ERROR -> $it" )
            }
            return@withContext emptyList()
        }
    }

    override suspend fun fetchLyricsFor(song: Song?): List<Lyric> = withContext( ioDispatcher ) {
        try {
            val path = song?.lyricPath() ?: return@withContext emptyList()
            val lyricFile = File( path )

            if ( !lyricFile.exists() ) {
                Timber.tag( TAG ).e( "Lyrics file does not exist at: $path" )
                return@withContext emptyList()
            }
            val content = lyricFile.bufferedReader( Charsets.UTF_8 ).use { it.readText() }

            Lyrics.from( content )
        } catch ( e: Exception ) {
            Timber.tag( TAG )
                .e("Error occurred while fetching lyrics for: " +
                        "${song?.title} ${e.message}"
                )
            emptyList()
        }
    }

}

private fun buildSongUsing( cursor: Cursor ): Song {
    val mediaUri = cursor.getMediaUriFrom().toString()
    val mediaStoreId = cursor.getLongFrom( AudioColumns._ID )
    val dateAdded = cursor.getNullableLongFrom( AudioColumns.DATE_MODIFIED )
    val dateModified = cursor.getNullableLongFrom( AudioColumns.DATE_MODIFIED )
    val title = cursor.getStringFrom( AudioColumns.TITLE )
    val albumId = cursor.getLongFrom(AudioColumns.ALBUM_ID )
    val artist = cursor.getNullableStringFrom( AudioColumns.ARTIST ) ?: ""
    return Song(
        id = mediaUri,
        mediaStoreId = mediaStoreId,
        mediaUri = mediaUri,
        title = title,
        trackNumber = cursor.getNullableIntFrom( AudioColumns.TRACK ) ?: 0,
        year = cursor.getNullableIntFrom( AudioColumns.YEAR ) ?: 0,
        duration = cursor.getLongFrom( AudioColumns.DURATION ),
        albumId = albumId,
        albumTitle = cursor.getNullableStringFrom( AudioColumns.ALBUM ) ?: "",
        artist = artist,
        composer = cursor.getNullableStringFrom( AudioColumns.COMPOSER ) ?: "",
        dateModified = dateAdded ?: dateModified ?: 0L,
        size = cursor.getNullableLongFrom( AudioColumns.SIZE ) ?: 0,
        path = cursor.getNullableStringFrom( AudioColumns.DATA ) ?: "",
        artworkUri = cursor.getArtworkUri()?.toString(),
        albumArtist =
            cursor.getNullableStringFrom( AudioColumns.ALBUM_ARTIST )
                ?: artist,
        artistId = cursor.getLongFrom( AudioColumns.ARTIST_ID )
    )
}


private fun Cursor.getMediaUriFrom(): Uri = ContentUris.withAppendedId(
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    getLongFrom( AudioColumns._ID
    )
)

private fun Cursor.getArtworkUri(): Uri? = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    .buildUpon()
    .run {
        appendPath( getLongFrom( AudioColumns._ID ).toString() )
        appendPath( "albumart" )
        build()
    }

private fun Cursor.getNullableLongFrom( columnName: String ): Long? {
    val columnIndex = getColumnIndex( columnName )
    return getLongOrNull( columnIndex )
}

private fun Cursor.getNullableIntFrom( columnName: String ): Int? {
    val columnIndex = getColumnIndex( columnName )
    return getIntOrNull( columnIndex )
}

private fun Cursor.getStringFrom( columnName: String ): String {
    val columnIndex = getColumnIndex( columnName );
    return getString( columnIndex )
}

private fun Song.lyricPath() =
    SimplePath( path ).let {
        it.parent?.join( it.nameWithoutExtension + ".lrc" )?.pathString
    }

@Immutable
private class SimplePath( val parts: List<String> ) {
    constructor( path: String ) : this( normalize ( p ( path ) ) )
    constructor( path: SimplePath, vararg subParts: String ) :
            this( normalize( path.parts + p( *subParts ) ) )

    val name get() = parts.last()
    val nameWithoutExtension get() = name.substringBeforeLast( "." )
    val extension get() = name.substringAfterLast( ".", "" )
    val parent get() = if ( parts.size > 1 ) {
        SimplePath( parts.subList( 0, parts.lastIndex ) )
    } else {
        null
    }
    val size get() = parts.size
    val pathString get() = parts.joinToString( "/" )

    fun join( vararg nParts: String ) = SimplePath( this, *nParts )

    override fun toString() = pathString

    companion object {
        private fun p( vararg path: String ) = path.fold( listOf<String>() ) { prev, curr ->
            prev + curr.split( "/", "\\" )
        }

        private fun normalize( parts: List<String> ): List<String> {
            val normalizedPath = mutableListOf<String>()
            for ( part in parts ) {
                when {
                    part.isEmpty() -> {}
                    part == "." -> {}
                    part == ".." -> normalizedPath.removeAt( normalizedPath.lastIndex )
                    else -> normalizedPath.add( part )
                }
            }
            return normalizedPath
        }
    }
}

val projection = arrayOf(
    AudioColumns._ID,
    AudioColumns.DATE_ADDED,
    AudioColumns.DATE_MODIFIED,
    AudioColumns.TITLE,
    AudioColumns.TRACK,
    AudioColumns.YEAR,
    AudioColumns.DURATION,
    AudioColumns.ALBUM_ID,
    AudioColumns.ALBUM,
    AudioColumns.ARTIST_ID,
    AudioColumns.ARTIST,
    AudioColumns.COMPOSER,
    AudioColumns.SIZE,
    AudioColumns.DATA,
    AudioColumns.ALBUM_ID,
)

private fun SortSongsBy.toMediaStoreSortFormat() = when ( this ) {
    SortSongsBy.TITLE -> MediaStore.Audio.Media.TITLE
    SortSongsBy.ARTIST -> MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
    SortSongsBy.ALBUM -> MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
    SortSongsBy.YEAR -> MediaStore.Audio.Media.YEAR
    SortSongsBy.DURATION -> MediaStore.Audio.Media.DURATION
    SortSongsBy.DATE_ADDED -> MediaStore.Audio.Media.DATE_ADDED
    SortSongsBy.COMPOSER -> MediaStore.Audio.Media.COMPOSER
    SortSongsBy.CUSTOM -> MediaStore.Audio.Media.DEFAULT_SORT_ORDER
}



private const val TAG = "SONGS-STORE"