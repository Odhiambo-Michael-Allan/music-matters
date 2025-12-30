package com.squad.musicmatters.feature.queue.components

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.ui.IconTextBody
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongCard
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@OptIn( ExperimentalFoundationApi::class )
@Composable
internal fun QueueList(
    songsInQueue: List<Song>,
    currentlyPlayingSongId: String,
    language: Language,
    songsAdditionalMetadata: List<SongAdditionalMetadataInfo>,
    favoriteSongIds: Set<String>,
    onFavorite: ( String, Boolean ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
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
    onSaveQueue: ( List<Song> ) -> Unit,
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
        onMove = {
//            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        },
        onCommit = { orderedList ->
            onSaveQueue( orderedList )
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
                Text( language.damnThisIsSoEmpty )
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
                            language = language,
                            song = song,
                            isCurrentlyPlaying = currentlyPlayingSongId == song.id,
                            isFavorite = favoriteSongIds.contains( song.id ),
                            playlistInfos = onGetPlaylists(),
                            onClick = { playSong( song, songsInQueue ) },
                            onFavorite = onFavorite,
                            onPlayNext = onPlayNext,
                            onAddToQueue = onAddToQueue,
                            onViewArtist = onViewArtist,
                            onViewAlbum = onViewAlbum,
                            onShareSong = onShareSong,
                            onDragHandleClick = {},
                            onGetSongsInPlaylist = { playlist ->
                                songsInQueue.filter { song -> playlist.songIds.contains( song.id ) }
                            },
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onCreatePlaylist = onCreatePlaylist,
                            onDeleteSong = onDeleteSong,
                            onGetSongAdditionalMetadata = {
                                songsAdditionalMetadata.find { it.songId == song.id }
                            },
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
    language: Language,
    song: Song,
    isCurrentlyPlaying: Boolean,
    isFavorite: Boolean,
    playlistInfos: List<PlaylistInfo>,
    onClick: () -> Unit,
    onFavorite: ( String, Boolean ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: (String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDragHandleClick: () -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onGetSongAdditionalMetadata: () -> SongAdditionalMetadataInfo?,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            modifier = modifier.draggableHandle(),
            onClick = onDragHandleClick
        ) {
            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = null
            )
        }
        SongCard(
            language = language,
            song = song,
            isCurrentlyPlaying = isCurrentlyPlaying,
            isFavorite = isFavorite,
            playlistInfos = playlistInfos,
            onClick = onClick,
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
            onGetSongAdditionalMetadata = onGetSongAdditionalMetadata,
            onDeleteSong = onDeleteSong,
        )
    }
}

@DevicePreviews
@Composable
private fun QueueListPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = DefaultPreferences.FONT_SCALE,
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME
    ) {
        QueueList(
            songsInQueue = previewData.songs,
            language = English,
            songsAdditionalMetadata = emptyList(),
            currentlyPlayingSongId = previewData.songs.first().id,
            favoriteSongIds = setOf( previewData.songs.first().id ),
            playSong = { _, _ -> },
            onPlayNext = {},
            onGetPlaylists = { emptyList() },
            onAddToQueue = {},
            onMove = { _, _ -> },
            onViewAlbum = {},
            onShareSong = {},
            onViewArtist = {},
            onDeleteSong = {},
            onFavorite = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onAddSongsToPlaylist = { _, _ -> },
            onSearchSongsMatchingQuery = { emptyList() },
            onSaveQueue = {}
        )
    }
}