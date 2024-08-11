package com.odesa.musicMatters.core.data.utils

import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.model.Song
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

fun List<Song>.sortSongs( sortSongsBy: SortSongsBy, reverse: Boolean ): List<Song> {
    return when ( sortSongsBy ) {
        SortSongsBy.TITLE -> if ( reverse ) sortedByDescending { it.title } else sortedBy { it.title }
        SortSongsBy.ALBUM -> if ( reverse ) sortedByDescending { it.albumTitle } else sortedBy { it.albumTitle }
        SortSongsBy.ARTIST -> if ( reverse ) sortedByDescending { it.artists.joinToString() } else sortedBy { it.artists.joinToString() }
        SortSongsBy.COMPOSER -> if ( reverse ) sortedByDescending { it.composer } else sortedBy { it.composer }
        SortSongsBy.DURATION -> if ( reverse ) sortedByDescending { it.duration } else sortedBy { it.duration }
        SortSongsBy.YEAR -> if ( reverse ) sortedByDescending { it.year } else sortedBy { it.year }
        SortSongsBy.DATE_ADDED -> if ( reverse ) sortedByDescending { it.dateModified } else sortedBy { it.dateModified }
        SortSongsBy.FILENAME -> if ( reverse ) sortedByDescending { it.path } else sortedBy { it.path }
        SortSongsBy.TRACK_NUMBER -> if ( reverse ) sortedByDescending { it.trackNumber } else sortedBy { it.trackNumber }
        SortSongsBy.CUSTOM -> shuffled()
    }
}