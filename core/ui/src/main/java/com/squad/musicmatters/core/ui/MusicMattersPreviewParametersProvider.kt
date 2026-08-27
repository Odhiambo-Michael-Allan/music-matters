package com.squad.musicmatters.core.ui

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Folder
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.Song
import java.util.UUID
import com.squad.musicmatters.core.model.Playlist

class MusicMattersPreviewParametersProvider : PreviewParameterProvider<PreviewData> {

    override val values: Sequence<PreviewData>
        get() = sequenceOf(
            PreviewData(
                songs = PreviewParameterData.songs,
                albums = PreviewParameterData.albums,
                artists = PreviewParameterData.artists,
                genres = PreviewParameterData.genres,
                playlists = PreviewParameterData.playlists,
                folders = PreviewParameterData.folders,
            )
        )

}

data class PreviewData(
    val songs: List<Song>,
    val albums: List<Album>,
    val artists: List<Artist>,
    val genres: List<Genre>,
    val playlists: List<Playlist>,
    val folders: List<Folder>,
)

object PreviewParameterData {
    val songs = listOf(
        Song(
            id = "id1",
            mediaStoreId = 0,
            mediaUri = "Uri.EMPTY",
            title = "You're On ( feat. Kyan )",
            albumId = 0L,
            albumTitle = "D",
            artist = "A - Michael Jackson",
            artworkUri = "/storage/Uri.Empty",
            composer = "A,B",
            dateModified = 354L,
            duration = 60L,
            trackNumber = 324,
            year = 2022,
            size = 1L,
            path = "/path/to/song/7",
            artistId = 0,
        ),
        Song(
            id = "id2",
            mediaStoreId = 0,
            mediaUri = "Uri.EMPTY",
            title = "Silk Music Showcase 07 ( Mixed by Jacob Henry & Tom Fall )",
            albumTitle = "C",
            artist = "B - Michael Jackson",
            artworkUri = null,
            composer = "B,C",
            dateModified = 754L,
            albumId = 0L,
            duration = 4L,
            trackNumber = 235,
            year = 2002,
            size = 2L,
            path = "/path/to/song/8",
            artistId = 0,
        ),
        Song(
            id = "id3",
            mediaStoreId = 0,
            mediaUri = "Uri.EMPTY",
            title = "Ric Flair Drip ( with Metro Boomin )",
            albumTitle = "B",
            artist = "C - Michael Jackson",
            artworkUri = null,
            composer = "C,D",
            dateModified = 7976L,
            albumId = 0L,
            duration = 7L,
            trackNumber = 443,
            year = 2007,
            size = 3L,
            path = "/path/to/song/6",
            artistId = 0,
        ),
        Song(
            id = "id4",
            mediaStoreId = 0,
            mediaUri = "Uri.EMPTY",
            title = "Dear Boy",
            albumTitle = "A",
            artist = "D - Michael Jackson",
            artworkUri = null,
            composer = "D,E",
            dateModified = 200L,
            albumId = 0L,
            duration = 4L,
            trackNumber = 234,
            year = 2004,
            size = 4L,
            path = "/path/to/song/1",
            artistId = 0,
        ),
        Song(
            id = "id5",
            mediaStoreId = 0,
            mediaUri = "Uri.EMPTY",
            title = "The Days",
            albumTitle = "<unknown>",
            artist = "E - Michael Jackson",
            artworkUri = null,
            composer = null,
            dateModified = 34245L,
            albumId = 0L,
            duration = 89L,
            trackNumber =134,
            year = 1990,
            size = 5L,
            path = "/path/to/song/5",
            artistId = 0,
        ),
    )

    val albums = listOf(
        Album(
            id = 1L,
            title = "The Eminem Show",
            artist = "Eminem",
            trackCount = 20,
            artworkUri = null,
            artistId = 0L,
        ),
        Album(
            id = 2L,
            title = "The Score",
            artist = "Fugees",
            trackCount = 17,
            artworkUri = null,
            artistId = 0L,
        ),
        Album(
            id = 3L,
            title = "The Marshall Mathers LP",
            artist = "Eminem",
            trackCount = 18,
            artworkUri = null,
            artistId = 0L,
        ),
        Album(
            id = 4L,
            title = "The Miseducation of Lauryn Hill",
            artist = "Lauryn Hill",
            trackCount = 16,
            artworkUri = null,
            artistId = 0L,
        ),
        Album(
            id = 5L,
            title = "Please Hammer, Don't Hurt 'Em",
            artist = "MC Hammer",
            trackCount = 14,
            artworkUri = null,
            artistId = 0L,
        )
    )

    val artists = listOf(
        Artist(
            id = 1,
            name = "Drake",
            trackCount = 2,
            artworkUri = null,
        ),
        Artist(
            id = 2,
            name = "Alicia Keys",
            trackCount = 3,
            artworkUri = null,
        ),
        Artist(
            id = 3,
            name = "Zedd",
            trackCount = 1,
            artworkUri = null,
        )
    )

    val genres = listOf(
        Genre(
            id = 1,
            name = "Rap/HipHop",
            numberOfTracks = 200,
        ),
        Genre(
            id = 2,
            name = "",
            numberOfTracks = 30,
        ),
        Genre(
            id = 3,
            name = "RnB",
            numberOfTracks = 37,
        ),
        Genre(
            id = 4,
            name = "Pop",
            numberOfTracks = 57,
        ),
        Genre(
            id = 5,
            name = "Alternative",
            numberOfTracks = 5,
        )
    )

    val playlists = List( 20 ) {
        Playlist(
            id = UUID.randomUUID().toString() + "$it",
            title = "Playlist-$it",
            songIds = emptySet(),
            artworkUri = "",
        )
    }.toMutableList()

    val folders = listOf(
        Folder(
            name = "song-1",
            path = "path/to/song-1",
            trackCount = 2
        ),
        Folder(
            name = "song-2",
            path = "path/to/song-2",
            trackCount = 4
        ),
        Folder(
            name = "song-3",
            path = "path/to/song-3",
            trackCount = 2
        ),
        Folder(
            name = "song-4",
            path = "path/to/song-4",
            trackCount = 18
        ),
        Folder(
            name = "song-7",
            path = "path/to/song-7",
            trackCount = 67
        ),
    )
}

