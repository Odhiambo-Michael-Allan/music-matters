package com.squad.musicmatters.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.squad.musicmatters.core.model.Song
import java.util.Calendar
import java.util.UUID
import com.squad.musicmatters.core.model.PlaylistInfo

class MusicMattersPreviewParametersProvider : PreviewParameterProvider<PreviewData> {

    override val values: Sequence<PreviewData>
        get() = sequenceOf(
            PreviewData(
                songs = PreviewParameterData.songs,
                playlists = PreviewParameterData.playlistInfos
            )
        )

}

data class PreviewData(
    val songs: List<Song>,
    val playlists: List<PlaylistInfo>,
)

object PreviewParameterData {
    val songs = listOf(
        Song(
            id = "id1",
            mediaUri = "Uri.EMPTY",
            title = "You're On ( feat. Kyan )",
            albumTitle = "D",
            artists = setOf( "A", "Michael Jackson" ),
            artworkUri = "/storage/Uri.Empty",
            composer = "A,B",
            dateModified = 354L,
            displayTitle = "song-1",
            duration = 60L,
            trackNumber = 324,
            year = 2022,
            size = 1L,
            path = "/path/to/song/7"
        ),
        Song(
            id = "id2",
            mediaUri = "Uri.EMPTY",
            title = "song-2",
            albumTitle = "C",
            artists = setOf( "B", "Michael Jackson" ),
            artworkUri = null,
            composer = "B,C",
            dateModified = 754L,
            displayTitle = "Silk Music Showcase 07 ( Mixed by Jacob Henry & Tom Fall )",
            duration = 4L,
            trackNumber = 235,
            year = 2002,
            size = 2L,
            path = "/path/to/song/8"
        ),
        Song(
            id = "id3",
            mediaUri = "Uri.EMPTY",
            title = "Ric Flair Drip ( with Metro Boomin )",
            albumTitle = "B",
            artists = setOf( "C", "Metro Boomin", "Offset", "Drake" ),
            artworkUri = null,
            composer = "C,D",
            dateModified = 7976L,
            displayTitle = "song-3",
            duration = 7L,
            trackNumber = 443,
            year = 2007,
            size = 3L,
            path = "/path/to/song/6"
        ),
        Song(
            id = "id4",
            mediaUri = "Uri.EMPTY",
            title = "Dear Boy",
            albumTitle = "A",
            artists = setOf( "D", "Avicii" ),
            artworkUri = null,
            composer = "D,E",
            dateModified = 200L,
            displayTitle = "song-4",
            duration = 4L,
            trackNumber = 234,
            year = 2004,
            size = 4L,
            path = "/path/to/song/1"
        ),
        Song(
            id = "id5",
            mediaUri = "Uri.EMPTY",
            title = "The Days",
            albumTitle = "<unknown>",
            artists = setOf( "E", "Avicii" ),
            artworkUri = null,
            composer = null,
            dateModified = 34245L,
            displayTitle = "song-3",
            duration = 89L,
            trackNumber =134,
            year = 1990,
            size = 5L,
            path = "/path/to/song/5"
        ),
    )

    val playlistInfos = List( 20 ) {
        PlaylistInfo(
            id = UUID.randomUUID().toString() + "$it",
            title = "Playlist-$it",
            songIds = emptySet()
        )
    }.toMutableList()
}

