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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
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
    language: Language,
    songsAdditionalMetadata: List<SongAdditionalMetadata>,
    favoriteSongIds: Set<String>,
    playlists: List<Playlist>,
    onFavorite: ( Song, Boolean ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onSaveQueue: ( List<Song> ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
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
                            playlists = playlists,
                            songAdditionalMetadata = songsAdditionalMetadata.find { it.songId == song.id },
                            onClick = { playSong( song, songsInQueue ) },
                            onFavorite = onFavorite,
                            onPlayNext = onPlayNext,
                            onAddToQueue = onAddToQueue,
                            onViewArtist = onViewArtist,
                            onViewAlbum = onViewAlbum,
                            onShareSong = onShareSong,
                            onDragHandleClick = {},
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

@Composable
private fun ReorderableCollectionItemScope.QueueSongCard(
    modifier: Modifier = Modifier,
    language: Language,
    song: Song,
    isCurrentlyPlaying: Boolean,
    isFavorite: Boolean,
    songAdditionalMetadata: SongAdditionalMetadata?,
    playlists: List<Playlist>,
    onClick: () -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDragHandleClick: () -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
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
                imageVector = MusicMattersIcons.DragHandle,
                contentDescription = null
            )
        }
        SongCard(
            language = language,
            song = song,
            isCurrentlyPlaying = isCurrentlyPlaying,
            isFavorite = isFavorite,
            playlists = playlists,
            songAdditionalMetadata = songAdditionalMetadata,
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
        fontName = SupportedFonts.GoogleSans.name,
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
            playlists = previewData.playlists,
            playSong = { _, _ -> },
            onPlayNext = {},
            onAddToQueue = {},
            onViewAlbum = {},
            onShareSong = {},
            onViewArtist = {},
            onDeleteSong = {},
            onFavorite = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onAddSongsToPlaylist = { _, _ -> },
            onSaveQueue = {},
            onShowSnackBar = {},
        )
    }
}