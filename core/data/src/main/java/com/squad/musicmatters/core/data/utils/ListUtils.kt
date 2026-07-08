package com.squad.musicmatters.core.data.utils

import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.model.SortSongsBy
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

fun <T> List<T>.subListNonStrict(length: Int, start: Int = 0 ) =
    subList( start, min( start + length, size ) )

fun <T> List<T>.randomSubList( length: Int ): List<T> {
    val mut = toMutableList()
    val out = mutableListOf<T>()
    val possibleLength = max( 0, min( length, mut.size ) )
    for ( i in 0 until possibleLength ) {
        val index = Random.nextInt( mut.size )
        out.add( mut.removeAt( index ) )
    }
    return out
}

fun List<Song>.sortSongs(by: SortSongsBy, reverse: Boolean ): List<Song> {
    val sortedList = when ( by ) {
        SortSongsBy.TITLE -> sortedBy { it.title }
        SortSongsBy.ALBUM -> sortedBy { it.albumTitle }
        SortSongsBy.ARTIST -> sortedBy { it.artist }
        SortSongsBy.COMPOSER -> sortedBy { it.composer }
        SortSongsBy.DURATION -> sortedBy { it.duration }
        SortSongsBy.YEAR -> sortedBy { it.year }
        SortSongsBy.DATE_ADDED -> sortedBy { it.dateModified }
        SortSongsBy.FILENAME -> sortedBy { it.path }
        SortSongsBy.TRACK_NUMBER -> sortedBy { it.trackNumber }
        SortSongsBy.CUSTOM -> shuffled()
    }
    return if ( reverse ) sortedList.reversed() else sortedList
}

fun List<Album>.sortAlbums(by: SortAlbumsBy, reverse: Boolean ): List<Album> {
    val sortedList = when ( by ) {
        SortAlbumsBy.ALBUM_NAME -> sortedBy { it.title }
        SortAlbumsBy.ARTIST_NAME -> sortedBy { it.artist }
        SortAlbumsBy.TRACK_COUNT -> sortedBy { it.trackCount }
        SortAlbumsBy.CUSTOM -> shuffled()
    }
    return if ( reverse ) sortedList.reversed() else sortedList
}

fun List<Artist>.sortArtists( by: SortArtistsBy, reverse: Boolean ): List<Artist> {
    val sortedList = when ( by ) {
        SortArtistsBy.ARTIST_NAME -> sortedBy { it.name }
        SortArtistsBy.TRACK_COUNT -> sortedBy { it.trackCount }
        SortArtistsBy.CUSTOM -> shuffled()
    }
    return if ( reverse ) sortedList.reversed() else sortedList
}