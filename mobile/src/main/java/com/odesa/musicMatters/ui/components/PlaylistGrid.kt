package com.odesa.musicMatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song

@Composable
fun PlaylistGrid(
    playlistInfos: List<PlaylistInfo>,
    isLoadingSongsInPlaylist: Boolean,
    language: Language,
    sortType: SortPlaylistsBy,
    sortReverse: Boolean,
    @DrawableRes fallbackResourceId: Int,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: ( SortPlaylistsBy ) -> Unit,
    onPlaylistClick: ( String, String ) -> Unit,
    onPlaySongsInPlaylist: (PlaylistInfo ) -> Unit,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onPlaySongsInPlaylistNext: (PlaylistInfo ) -> Unit,
    onAddSongsInPlaylistToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onShufflePlaySongsInPlaylist: (PlaylistInfo ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onAddSongsInPlaylistToQueue: (PlaylistInfo ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onDeletePlaylist: (PlaylistInfo ) -> Unit,
    playlistIsDeletable: (PlaylistInfo ) -> Boolean,
    onRenamePlaylist: (PlaylistInfo, String ) -> Unit,
) {
    MediaSortBarScaffold(
        mediaSortBar = {
            MediaSortBar(
                sortReverse = sortReverse,
                onSortReverseChange = onSortReverseChange,
                sortType = sortType,
                sortTypes = SortPlaylistsBy.entries.associateBy({ it }, { it.label(language) }),
                onSortTypeChange = onSortTypeChange,
                label = {
                    Text(
                        text = language.xPlaylists( playlistInfos.size.toString() )
                    )
                }
            )
        }
    ) {
        when {
            isLoadingSongsInPlaylist -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = null
                    )
                }
            ) {
                Text( text = language.damnThisIsSoEmpty )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive( minSize = 128.dp ),
                    contentPadding = PaddingValues( start = 8.dp, end = 8.dp,
                        top = 8.dp, bottom = 150.dp )
                ) {
                    items( playlistInfos ) {
                        PlaylistTile(
                            modifier = Modifier.fillMaxWidth(),
                            playList = it,
                            language = language,
                            fallbackResourceId = fallbackResourceId,
                            playlistIsDeletable = playlistIsDeletable( it ),
                            onPlaySongsInPlaylist = { onPlaySongsInPlaylist( it ) },
                            onPlayNext = { onPlaySongsInPlaylistNext( it ) },
                            onShufflePlay = { onShufflePlaySongsInPlaylist( it ) },
                            onPlaylistClick = { onPlaylistClick( it.id, it.title ) },
                            onCreatePlaylist = onCreatePlaylist,
                            onGetPlaylists = onGetPlaylists,
                            onAddToQueue = { onAddSongsInPlaylistToQueue( it ) },
                            onAddSongsInPlaylistToPlaylist = onAddSongsInPlaylistToPlaylist,
                            onGetSongsInPlaylist = onGetSongsInPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onDeletePlaylist = onDeletePlaylist,
                            onRenamePlaylist = onRenamePlaylist
                        )
                    }
                }
            }
        }
    }
}

fun SortPlaylistsBy.label(language: Language ) = when ( this ) {
    SortPlaylistsBy.TITLE -> language.title
    SortPlaylistsBy.CUSTOM -> language.custom
    SortPlaylistsBy.TRACKS_COUNT -> language.trackCount
}

@Preview( showSystemUi = true )
@Composable
fun PlaylistGridPreview() {
    PlaylistGrid(
        playlistInfos = testPlaylistInfos,
        isLoadingSongsInPlaylist = false,
        language = English,
        sortType = SortPlaylistsBy.CUSTOM,
        sortReverse = false,
        fallbackResourceId = R.drawable.placeholder_light,
        onSortReverseChange = {},
        onSortTypeChange = {},
        onPlaylistClick = { _, _ -> },
        onPlaySongsInPlaylist = {},
        onGetSongsInPlaylist = { emptyList() },
        onAddSongsInPlaylistToPlaylist = { _, _ -> },
        onPlaySongsInPlaylistNext = {},
        onSearchSongsMatchingQuery = { emptyList() },
        onAddSongsInPlaylistToQueue = {},
        onCreatePlaylist = { _, _ -> },
        onGetPlaylists = { emptyList() },
        onShufflePlaySongsInPlaylist = {},
        onDeletePlaylist = {},
        playlistIsDeletable = { true },
        onRenamePlaylist = { _, _ -> }
    )
}