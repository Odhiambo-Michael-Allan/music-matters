package com.squad.musicmatters.core.ui

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode

@Composable
fun SongList(
    sortReverse: Boolean,
    sortSongsBy: SortSongsBy,
    language: Language,
    songs: List<Song>,
    playlistInfos: List<PlaylistInfo>,
    onShufflePlay: () -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onSortReverseChange: ( Boolean ) -> Unit,
    currentlyPlayingSongId: String,
    playSong: ( Song, List<Song> ) -> Unit,
    isFavorite: ( String ) -> Boolean,
    onFavorite: ( String, Boolean ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetAdditionalMetadataForSongWithId: ( String ) -> SongAdditionalMetadataInfo?,
    onDeleteSong: ( Song ) -> Unit,
    leadingContent: ( LazyListScope.() -> Unit )? = null
) {

    Column {
        MediaSortBar(
            sortReverse = sortReverse,
            onSortReverseChange = onSortReverseChange,
            sortType = sortSongsBy,
            sortTypes = SortSongsBy.entries.associateBy(
                    { it },
                    { it.sortSongsByLabel( language ) }
                ),
            onSortTypeChange = onSortTypeChange,
            label = {
                Text(
                    text = language.xSongs( songs.size.toString() ),
                    fontWeight = FontWeight.SemiBold,
                )
            },
            onShufflePlay = onShufflePlay
        )

        when {
            songs.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null
                    )
                },
                content = {
                    Text(
                        language.damnThisIsSoEmpty,
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            )
            else -> {
                val lazyListState = rememberLazyListState()

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.drawScrollBar( lazyListState )
                ) {
                    leadingContent?.invoke( this )
                    itemsIndexed( songs ) {  index, song ->
                        SongCard(
                            language = language,
                            song = song,
                            isCurrentlyPlaying = currentlyPlayingSongId == song.id,
                            isFavorite = isFavorite( songs[ index ].id ),
                            playlists = playlistInfos,
                            onClick = { playSong( song, songs ) },
                            onFavorite = onFavorite,
                            onPlayNext = onPlayNext,
                            onAddToQueue = onAddToQueue,
                            onViewArtist = onViewArtist,
                            onViewAlbum = onViewAlbum,
                            onShareSong = onShareSong,
                            onGetSongsInPlaylist = onGetSongsInPlaylist,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onCreatePlaylist = onCreatePlaylist,
                            onGetSongAdditionalMetadata = {
                                onGetAdditionalMetadataForSongWithId( song.id )
                            },
                            onDeleteSong = onDeleteSong
                        )
                    }
                }
            }
        }
    }
}

fun SortSongsBy.sortSongsByLabel(language: Language) = when ( this ) {
    SortSongsBy.CUSTOM -> language.custom
    SortSongsBy.TITLE -> language.title
    SortSongsBy.ARTIST -> language.artist
    SortSongsBy.ALBUM -> language.album
    SortSongsBy.DURATION -> language.duration
    SortSongsBy.DATE_ADDED -> language.dateAdded
    SortSongsBy.COMPOSER -> language.composer
    SortSongsBy.YEAR -> language.year
    SortSongsBy.FILENAME -> language.filename
    SortSongsBy.TRACK_NUMBER -> language.trackNumber
}



@DevicePreviews
@Composable
fun SongListPreview() {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = 1.25f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        SongList(
            sortReverse = false,
            sortSongsBy = SortSongsBy.TITLE,
            language = English,
            songs = PreviewParameterData.songs,
            playlistInfos = emptyList(),
            onShufflePlay = {},
            onSortTypeChange = {},
            onSortReverseChange = {},
            isFavorite = { true },
            onFavorite = { _, _ -> },
            currentlyPlayingSongId = PreviewParameterData.songs.first().id,
            playSong = { _, _ -> },
            onViewAlbum = {},
            onViewArtist = {},
            onShareSong = {},
            onPlayNext = {},
            onAddToQueue = {},
            onGetSongsInPlaylist = { emptyList() },
            onAddSongsToPlaylist = { _, _ -> },
            onSearchSongsMatchingQuery = { emptyList() },
            onCreatePlaylist = { _, _ -> },
            onGetAdditionalMetadataForSongWithId = { null },
            onDeleteSong = {}
        )
    }
}



