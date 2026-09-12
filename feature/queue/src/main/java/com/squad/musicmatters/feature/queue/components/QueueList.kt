package com.squad.musicmatters.feature.queue.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.i8n.R as i8nR
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.ui.IconTextBody
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongCard
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem

@OptIn( ExperimentalFoundationApi::class )
@Composable
internal fun QueueList(
    songsInQueue: List<Song>,
    currentlyPlayingSongId: String,
    songsAdditionalMetadata: List<SongMetadata>,
    onGetPlaylists: () -> List<Playlist>,
    onFavorite: ( Song, Boolean ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onMoveSong: ( Int, Int ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    isFavorite: ( Song ) -> Boolean,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onRemoveFromQueue: ( Song ) -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = songsInQueue.indexOfFirst {
            it.id == currentlyPlayingSongId
        }
    )
    // Show the scroll to top button if the first visible item is past the 10th item. We use
    // a remembered derived state to minimize unnecessary compositions
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 10
        }
    }

    val ( displayItems, reorderableState ) = rememberReorderableLazyListDataSource(
        listState = listState,
        items = songsInQueue,
        itemKey = Song::id,
        onCommit = { from, to ->
            onMoveSong( from, to )
        },
    )

    when {
        songsInQueue.isEmpty() -> IconTextBody(
            icon = {
                Icon(
                    modifier = it,
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = null
                )
            },
            content = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty )
                )
            }
        )
        else -> {
            Box {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues( bottom = 70.dp ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        displayItems,
                        { it.id }
                    ) { song ->
                        ReorderableItem(
                            state = reorderableState,
                            key = song.id
                        ) {
                            QueueSongCard(
                                song = song,
                                isCurrentlyPlaying = currentlyPlayingSongId == song.id,
                                songsAdditionalMetadata = songsAdditionalMetadata,
                                onGetPlaylists = onGetPlaylists,
                                onClick = { playSong( song, songsInQueue ) },
                                onDragHandleClick = {},
                                onFavorite = onFavorite,
                                onPlayNext = onPlayNext,
                                onAddToQueue = onAddToQueue,
                                onAddSongsToPlaylist = onAddSongsToPlaylist,
                                onViewAlbum = onViewAlbum,
                                onViewArtist = onViewArtist,
                                onShareSong = onShareSong,
                                onCreatePlaylist = onCreatePlaylist,
                                onShowSnackBar = onShowSnackBar,
                                onDeleteSong = onDeleteSong,
                                isFavorite = isFavorite,
                                onSongIsPresentInQueue = onSongIsPresentInQueue,
                                onRemoveFromQueue = onRemoveFromQueue,
                            )
                        }
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

@Composable
private fun ReorderableCollectionItemScope.QueueSongCard(
    modifier: Modifier = Modifier,
    song: Song,
    isCurrentlyPlaying: Boolean,
    songsAdditionalMetadata: List<SongMetadata>,
    onGetPlaylists: () -> List<Playlist>,
    onClick: () -> Unit,
    onDragHandleClick: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    isFavorite: ( Song ) -> Boolean,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onRemoveFromQueue: ( Song ) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors( containerColor = Color.Transparent ),
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 4.dp, 4.dp, 4.dp)
        ) {
            IconButton(
                modifier = modifier.draggableHandle(),
                onClick = onDragHandleClick
            ) {
                Icon(
                    imageVector = MusicMattersIcons.DragHandle,
                    contentDescription = null
                )
            }
            SongCard(
                song = song,
                isCurrentlyPlaying = isCurrentlyPlaying,
                isFavorite = { isFavorite( song ) },
                onGetPlaylists = onGetPlaylists,
                onGetSongMetadata = {
                    songsAdditionalMetadata.find { metadata -> metadata.songId == song.id }
                },
                onClick = onClick,
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
                onSongIsPresentInQueue = onSongIsPresentInQueue,
                onRemoveFromQueue = onRemoveFromQueue,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun QueueListPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        useMaterialYou = true,
        fontScale = DefaultPreferences.FONT_SCALE,
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME
    ) {
        QueueList(
            songsInQueue = previewData.songs,
            currentlyPlayingSongId = previewData.songs.first().id,
            playSong = { _, _ -> },
            onMoveSong = { _, _ -> },
            songsAdditionalMetadata = emptyList(),
            onGetPlaylists = { emptyList() },
            onFavorite = { _, _ -> },
            onPlayNext = {},
            onAddToQueue = {},
            onViewArtist = {},
            onViewAlbum = {},
            onShareSong = {},
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onDeleteSong = {},
            onShowSnackBar = {},
            isFavorite = { false },
            onSongIsPresentInQueue = { true },
            onRemoveFromQueue = {}
        )
    }
}