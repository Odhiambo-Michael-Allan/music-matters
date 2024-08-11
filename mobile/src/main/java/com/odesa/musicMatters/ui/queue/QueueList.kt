package com.odesa.musicMatters.ui.queue

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.ui.components.IconTextBody
import com.odesa.musicMatters.ui.components.QueueSongCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@OptIn( ExperimentalFoundationApi::class )
@Composable
fun QueueList(
    modifier: Modifier = Modifier,
    uiState: QueueScreenUiState,
    @DrawableRes fallbackResourceId: Int,
    isFavorite: ( String ) -> Boolean,
    onFavorite: ( String ) -> Unit,
    playSong: (Song) -> Unit,
    onMove: ( Int, Int ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onDeleteSong: ( Song ) -> Unit,
) {

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.currentlyPlayingSongIndex
    )
    val reorderableLazyColumnState = rememberReorderableLazyColumnState( lazyListState = lazyListState ) { from, to ->
        onMove( from.index, to.index )
    }

    when {
        uiState.songsInQueue.isEmpty() -> IconTextBody(
            icon = {
                Icon(
                    modifier = it,
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null
                )
            },
            content = {
                Text( uiState.language.damnThisIsSoEmpty )
            }
        )
        else -> {
            LazyColumn(
                state = lazyListState,
            ) {
                items( uiState.songsInQueue, { it.id } ) { song ->
                    ReorderableItem( reorderableLazyListState = reorderableLazyColumnState, key = song.id ) {
                        QueueSongCard(
                            modifier = modifier.draggableHandle(),
                            language = uiState.language,
                            song = song,
                            isCurrentlyPlaying = uiState.currentlyPlayingSongId == song.id,
                            isFavorite = isFavorite( song.id ),
                            fallbackResourceId = fallbackResourceId,
                            playlistInfos = onGetPlaylists(),
                            onClick = { playSong( song ) },
                            onFavorite = onFavorite,
                            onPlayNext = onPlayNext,
                            onAddToQueue = onAddToQueue,
                            onViewArtist = onViewArtist,
                            onViewAlbum = onViewAlbum,
                            onShareSong = onShareSong,
                            onDragHandleClick = {},
                            onGetSongsInPlaylist = { playlist ->
                                uiState.songsInQueue.filter { playlist.songIds.contains( it.id ) }
                            },
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onCreatePlaylist = onCreatePlaylist,
                            onDeleteSong = onDeleteSong,
                            onGetSongAdditionalMetadata = {
                                uiState.songsAdditionalMetadataList.find { it.id == song.id }
                            }
                        )
                    }
                }
            }
        }
    }
}