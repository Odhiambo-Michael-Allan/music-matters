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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode

@Composable
fun SongList(
    sortReverse: Boolean,
    sortSongsBy: SortSongsBy,
    songs: List<Song>,
    playlists: List<Playlist>,
    songsAdditionalMetadata: List<SongAdditionalMetadata>,
    onShufflePlay: () -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onSortReverseChange: ( Boolean ) -> Unit,
    currentlyPlayingSongId: String,
    playSong: ( Song, List<Song> ) -> Unit,
    isFavorite: ( String ) -> Boolean,
    onFavorite: ( Song, Boolean ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    leadingContent: ( LazyListScope.() -> Unit )? = null
) {

    Column {
        MediaSortBar(
            sortReverse = sortReverse,
            onSortReverseChange = onSortReverseChange,
            sortType = sortSongsBy,
            sortTypes = SortSongsBy.entries.associateBy(
                    { it },
                    { it.sortSongsByLabelResId() }
                ),
            onSortTypeChange = onSortTypeChange,
            label = {
                Text(
                    text = stringResource( id = R.string.core_i8n_n_songs, songs.size ),
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
                        text = stringResource( id = R.string.core_i8n_damn_this_is_so_empty ),
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
                            song = song,
                            isCurrentlyPlaying = currentlyPlayingSongId == song.id,
                            isFavorite = isFavorite( songs[ index ].id ),
                            playlists = playlists,
                            songAdditionalMetadata = songsAdditionalMetadata.find { metadata -> metadata.songId == song.id },
                            onClick = { playSong( song, songs ) },
                            onFavorite = onFavorite,
                            onPlayNext = onPlayNext,
                            onAddToQueue = onAddToQueue,
                            onViewArtist = onViewArtist,
                            onViewAlbum = onViewAlbum,
                            onShareSong = onShareSong,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onCreatePlaylist = onCreatePlaylist,
                            onDeleteSong = onDeleteSong,
                            onShowSnackBar = onShowSnackBar,
                        )
                    }
                }
            }
        }
    }
}

fun SortSongsBy.sortSongsByLabelResId() = when ( this ) {
    SortSongsBy.CUSTOM -> R.string.core_i8n_custom
    SortSongsBy.TITLE -> R.string.core_i8n_title
    SortSongsBy.ARTIST -> R.string.core_i8n_artist
    SortSongsBy.ALBUM -> R.string.core_i8n_album
    SortSongsBy.DURATION -> R.string.core_i8n_duration
    SortSongsBy.DATE_ADDED -> R.string.core_i8n_date_added
    SortSongsBy.COMPOSER -> R.string.core_i8n_composer
    SortSongsBy.YEAR -> R.string.core_i8n_year
    SortSongsBy.FILENAME -> R.string.core_i8n_file_name
    SortSongsBy.TRACK_NUMBER -> R.string.core_i8n_track_number
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
            songs = PreviewParameterData.songs,
            playlists = emptyList(),
            songsAdditionalMetadata = emptyList(),
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
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onDeleteSong = {},
            onShowSnackBar = {},
        )
    }
}



