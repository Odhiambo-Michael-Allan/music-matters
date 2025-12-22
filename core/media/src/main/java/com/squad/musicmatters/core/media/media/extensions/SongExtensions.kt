package com.squad.musicmatters.core.media.media.extensions

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.squad.musicmatters.core.model.Song
import java.util.Date

fun Song.toMediaItem(): MediaItem = MediaItem.Builder()
    .apply {
        setMediaId( id )
        setUri( mediaUri )
        setMediaMetadata(
            MediaMetadata.Builder().from( this@toMediaItem ).build()
        )
    }.build()

fun MediaMetadata.Builder.from( song: Song ): MediaMetadata.Builder {
    setTitle( song.title )
    setDisplayTitle( song.title )
    setArtist( song.artists.joinToString( "," ) )
    setArtworkUri( song.artworkUri?.toUri() ?: Uri.EMPTY )
    setIsPlayable( true )
    return this
}
val Song.sizeString
    get() = "%.2f MB".format( size.toDouble() / ( 1024 * 1024 ) )
val Song.dateModifiedString: String
    get() = java.text.SimpleDateFormat.getInstance().format( Date( dateModified * 1000 ) )



val artistTagSeparators = setOf( "Feat", "feat.", "Ft", "ft", ",", "&" )