package com.squad.musicmatters.core.model

import java.time.Duration

class Lyrics {
    companion object {
        private val lyricsLineSeparatorRegex = Regex( """\n|\r|\r\n""" )
        private val lyricLineFilterRegex = Regex( """^\[(\d+):(\d+)\.(\d+)](.*)""" )

        fun from( string: String ): List<Lyric> {
            var lastTime = Duration.ZERO

            return string.split( lyricsLineSeparatorRegex )
                .filter { it.isNotBlank() } // Skip empty lines
                .map { line ->
                    val match = lyricLineFilterRegex.matchEntire( line.trim() )

                    if ( match != null ) {
                        val minutes = match.groupValues[1].toLong()
                        val seconds = match.groupValues[2].toLong()
                        val fractionStr = match.groupValues[3]

                        // LRC uses hundredths (2 digits) or milliseconds (3 digits)
                        val millis = when ( fractionStr.length ) {
                            2 -> fractionStr.toLong() * 10 // Convert 50 -> 500ms
                            3 -> fractionStr.toLong()      // Already 500ms
                            else -> fractionStr.toLong()
                        }

                        lastTime = Duration.ofMinutes( minutes )
                            .plusSeconds( seconds )
                            .plusMillis( millis )

                        Lyric( lastTime, match.groupValues[4].trim() )
                    } else {
                        // For lines without a timestamp, inherit the last one
                        // or treat as metadata
                        Lyric( lastTime, line.trim() )
                    }
                }
        }
    }
}

data class Lyric(
    val timeStamp: Duration,
    val content: String
)