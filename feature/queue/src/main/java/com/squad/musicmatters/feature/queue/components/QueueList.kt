package com.squad.musicmatters.feature.queue.components

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.IconTextBody
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongCard
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem

@OptIn( ExperimentalFoundationApi::class )
@Composable
internal fun QueueList(
    songsInQueue: List<Song>,
    currentlyPlayingSongId: String,
//    songsAdditionalMetadata: List<SongAdditionalMetadata>,
//    playlists: List<Playlist>,
//    onFavorite: ( Song, Boolean ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
//    onPlayNext: ( Song ) -> Unit,
//    onAddToQueue: ( Song ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onViewAlbum: ( String ) -> Unit,
//    onShareSong: ( Uri ) -> Unit,
//    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onDeleteSong: ( Song ) -> Unit,
    onMoveSong: ( Int, Int ) -> Unit,
//    onShowSnackBar: ( String ) -> Unit,
) {

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = songsInQueue.indexOfFirst {
            it.id == currentlyPlayingSongId
        }
    )

    val ( displayItems, reorderableState ) = rememberReorderableLazyListDataSource(
        listState = lazyListState,
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
                    text = stringResource( id = R.string.core_i8n_damn_this_is_so_empty )
                )
            }
        )
        else -> {
            LazyColumn(
                state = lazyListState,
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
                            onClick = { playSong( song, songsInQueue ) },
                            onDragHandleClick = {},
                        )
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
    onClick: () -> Unit,
    onDragHandleClick: () -> Unit,
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
                .padding( 12.dp, 4.dp, 4.dp, 4.dp )
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
                isFavorite = false,
                songAdditionalMetadata = null,
                playlists = emptyList(),
                onClick = onClick,
                onFavorite = { _, _ -> },
                onPlayNext = {},
                onCreatePlaylist = { _, _ -> },
                onDeleteSong = {},
                onShareSong = {},
                onAddToQueue = {},
                onViewAlbum = {},
                onViewArtist = {},
                onAddSongsToPlaylist = {_, _ -> },
                onShowSnackBar = {},
                modifier = Modifier.weight( 0.9f ),
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
//        fontName = SupportedFonts.GoogleSans.name,
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
        )
    }
}