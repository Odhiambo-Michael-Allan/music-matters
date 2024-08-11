package com.odesa.musicMatters.core.common.media.extensions

import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * This file contains extension methods for the java.lang package
 */

/**
 * Helper method to check if a [String] contains another in a case insensitive way.
 */
fun String?.containsIgnoreCase( other: String? ) =
    if ( this != null && other != null ) {
        lowercase( Locale.getDefault() ).contains( other.lowercase( Locale.getDefault() ) )
    } else
        this == other

fun Long.formatMilliseconds() = formatToMinAndSec(
    TimeUnit.MILLISECONDS.toDays( this ).floorDiv( TimeUnit.DAYS.toDays( 1 ) ),
    TimeUnit.MILLISECONDS.toHours( this ) % TimeUnit.DAYS.toHours( 1 ),
    TimeUnit.MILLISECONDS.toMinutes( this ) % TimeUnit.HOURS.toMinutes( 1 ),
    TimeUnit.MILLISECONDS.toSeconds( this ) % TimeUnit.MINUTES.toSeconds( 1 )
)

private fun formatToMinAndSec( duration: Long, hours: Long, minutes: Long, seconds: Long ) = when {
    duration == 0L && hours == 0L -> String.format( "%02d:%02d", minutes, seconds )
    duration == 0L -> String.format( "%02d:%02d:%02d", hours, minutes, seconds )
    else -> String.format( "%02d:%02d:%02d:%02d", duration, hours, minutes, seconds )
}

fun <T> MutableList<T>.move( from: Int, to: Int ) {
    if ( from == to ) return
    val element = this.removeAt( from ) ?: return
    this.add( to, element )
}