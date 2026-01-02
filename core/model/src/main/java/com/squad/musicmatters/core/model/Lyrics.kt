package com.squad.musicmatters.core.model

import java.time.Duration

class Lyrics {

    companion object {
        val lyricsLineSeparatorRegex = Regex( """\n|\r|\r\n""" )
        val lyricLineFilterRegex = Regex( """^\[\s*(\d+):(\d+)\.(\d+)?\s*](.*)""" )

        fun from( string: String ): List<Lyric> {
            var lastTime = 0L
            val pairs = string.split( lyricsLineSeparatorRegex ).map { x ->
                val match = lyricLineFilterRegex.matchEntire( x )
                val pair = when {
                    match != null -> Duration
                        .ofMinutes( match.groupValues[1].toLong() )
                        .plusSeconds( match.groupValues[2].toLong() )
                        .plusMillis( match.groupValues[3].toLong() )
                        .toMillis() to match.groupValues[4].trim()

                    else -> lastTime to x.trim()
                }
                lastTime = pair.first
                pair
            }
            return pairs.map {
                Lyric(
                    Duration.ofMillis( it.first ),
                    it.second
                )
            }
        }
    }
}

data class Lyric(
    val timeStamp: Duration,
    val content: String
)