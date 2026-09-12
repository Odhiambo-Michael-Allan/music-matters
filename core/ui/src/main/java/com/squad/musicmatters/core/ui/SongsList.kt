package com.squad.musicmatters.core.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.i8n.R as i8nR
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun SongsList(
    sortSongsInReverse: Boolean,
    sortSongsBy: SortSongsBy,
    songs: List<Song>,
    onGetPlaylists: () -> List<Playlist>,
    onGetSongsAdditionalMetadata: () -> List<SongMetadata>,
    onShufflePlay: () -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onSortSongsInReverseChange: ( Boolean ) -> Unit,
    currentlyPlayingSongId: String,
    onPlaySong: (Song, List<Song> ) -> Unit,
    isFavorite: ( String ) -> Boolean,
    onFavorite: ( Song, Boolean ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onAddSongToQueue: (Song ) -> Unit,
    onRemoveSongFromQueue: (Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlaySongNext: (Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    leadingContent: ( LazyListScope.() -> Unit )? = null,
    additionalBottomSheetMenuItems: ( @Composable ( Song ) -> Unit )? = null
) {

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Show the scroll to top button if the first visible item is past the 10th item. We use
    // a remembered derived state to minimize unnecessary compositions
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 10
        }
    }
    
    Column {
        MediaSortBar(
            sortInReverse = sortSongsInReverse,
            onSortInReverseChange = onSortSongsInReverseChange,
            sortBy = sortSongsBy,
            sortTypes = SortSongsBy.entries.associateBy(
                    { it },
                    { it.sortSongsByLabelResId() }
                ),
            onSortTypeChange = onSortTypeChange,
            label = {
                Text(
                    text = stringResource(
                        id = if ( songs.size > 1 ) {
                            i8nR.string.core_i8n_n_songs
                        } else {
                            i8nR.string.core_i8n_one_song
                        },
                        songs.size
                    ),
                    fontWeight = FontWeight.Bold,
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
                        text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty ),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            else -> {
                Box {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues( bottom = 70.dp ),
                    ) {
                        leadingContent?.invoke( this )
                        itemsIndexed(
                            items = songs,
                            key = { _, song -> song.id }
                        ) {  index, song ->
                            SongCard(
                                modifier = Modifier.animateItem(),
                                song = song,
                                isCurrentlyPlaying = currentlyPlayingSongId == song.id,
                                isFavorite = { isFavorite( songs[ index ].id ) },
                                onGetPlaylists = onGetPlaylists,
                                onGetSongMetadata = {
                                    onGetSongsAdditionalMetadata()
                                        .find { metadata -> metadata.songId == song.id }
                                },
                                onClick = { onPlaySong( song, songs ) },
                                onFavorite = onFavorite,
                                onPlayNext = onPlaySongNext,
                                onAddToQueue = onAddSongToQueue,
                                onViewArtist = onViewArtist,
                                onViewAlbum = onViewAlbum,
                                onShareSong = onShareSong,
                                onAddSongsToPlaylist = onAddSongsToPlaylist,
                                onCreatePlaylist = onCreatePlaylist,
                                onDeleteSong = onDeleteSong,
                                onShowSnackBar = onShowSnackBar,
                                onRemoveFromQueue = onRemoveSongFromQueue,
                                onSongIsPresentInQueue = onSongIsPresentInQueue,
                                additionalBottomSheetMenuItems = additionalBottomSheetMenuItems,
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align( Alignment.BottomCenter )
                            .padding(
                                bottom = if ( currentlyPlayingSongId.isNotBlank() ) {
                                    70.dp
                                } else {
                                    8.dp
                                }
                            )
                    ) {
                        AnimatedVisibility(
                            visible = showScrollToTopButton
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        // Animate scroll to the first item
                                        listState.scrollToItem( index = 0 )
                                    }
                                }
                            ) {
                                Text(
                                    text = stringResource( id = i8nR.string.core_i8n_scroll_to_top ),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun SortSongsBy.sortSongsByLabelResId() = when ( this ) {
    SortSongsBy.CUSTOM -> i8nR.string.core_i8n_custom
    SortSongsBy.TITLE -> i8nR.string.core_i8n_title
    SortSongsBy.ARTIST -> i8nR.string.core_i8n_artist
    SortSongsBy.ALBUM -> i8nR.string.core_i8n_album
    SortSongsBy.DURATION -> i8nR.string.core_i8n_duration
    SortSongsBy.DATE_ADDED -> i8nR.string.core_i8n_date_added
    SortSongsBy.COMPOSER -> i8nR.string.core_i8n_composer
    SortSongsBy.YEAR -> i8nR.string.core_i8n_year
}



@DevicePreviews
@Composable
fun SongsListPreview() {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = 1.25f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        SongsList(
            sortSongsInReverse = false,
            sortSongsBy = SortSongsBy.TITLE,
            songs = PreviewParameterData.songs,
            onGetPlaylists = { emptyList() },
            onGetSongsAdditionalMetadata = { emptyList() },
            onShufflePlay = {},
            onSortTypeChange = {},
            onSortSongsInReverseChange = {},
            isFavorite = { true },
            onFavorite = { _, _ -> },
            currentlyPlayingSongId = PreviewParameterData.songs.first().id,
            onPlaySong = { _, _ -> },
            onViewAlbum = {},
            onViewArtist = {},
            onShareSong = {},
            onPlaySongNext = {},
            onAddSongToQueue = {},
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onDeleteSong = {},
            onShowSnackBar = {},
            onSongIsPresentInQueue = { true },
            onRemoveSongFromQueue = {}
        )
    }
}



