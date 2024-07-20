package com.odesa.musicMatters.core.data.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import com.odesa.musicMatters.core.model.Song
import timber.log.Timber
import java.io.File

// https://github.com/RetroMusicPlayer/RetroMusicPlayer/blob/dev/app/src/main/java/code/name/monkey/retromusic/util/MusicUtil.kt
object DeleteSongUtil {

    fun deleteSongs( context: Context, songs: List<Song> ) {
        val projection = arrayOf( BaseColumns._ID, DATA )
        val selection = StringBuilder()
        selection.append( BaseColumns._ID + " IN (" )
        for ( i in songs.indices ) {
            val songId = songs[ i ].mediaItem.mediaMetadata.extras?.getLong( MEDIA_ITEM_ID_KEY )
                ?: return
            selection.append( songId )
            if ( i < songs.size - 1 )
                selection.append( "," )
        }
        selection.append( ")" )
        var deletedCount = 0
        try {
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection.toString(),
                null, null
            )
            cursor?.let {
                it.moveToFirst()
                while ( !it.isAfterLast ) {
                    val id: Int = it.getInt( 0 )
                    val name: String = it.getString( 1 )
                    try {  // File.delete can throw a security exception
                        val file = File( name )
                        if ( file.delete() ) {
                            // Remove selected track from the database
                            context.contentResolver.delete(
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    id.toLong()
                                ), null, null
                            )
                            deletedCount++
                        } else {
                            // I'm not sure if we'd ever get here ( deletion would have to fail, but
                            // no exception thrown )
                            Timber.tag( TAG ).d( "Failed to delete file $name" )
                        }
                        it.moveToNext()
                    } catch ( exception: SecurityException ) {
                        it.moveToNext()
                    } catch ( exception: NullPointerException ) {
                        Timber.tag( TAG ).d( "Failed to find file $name" )
                    }
                }
                it.close()
            }
        } catch ( ignored: SecurityException ) {}
    }
}

private const val DATA = "_data"
private const val TAG = "--DELETE-SONG-UTIL-TAG--"
const val MEDIA_ITEM_ID_KEY = "--ID--"