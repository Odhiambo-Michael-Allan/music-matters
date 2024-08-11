package com.odesa.musicMatters.ui.components

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.SortPathsBy
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.ui.tree.TreeScreenUiState
import com.odesa.musicMatters.ui.tree.sortPathsByLabel
import com.odesa.musicMatters.ui.tree.testTreeScreenUiState


@Composable
fun TreeSongList(
    uiState: TreeScreenUiState,
    @DrawableRes fallbackResourceId: Int,
    togglePath: ( String ) -> Unit,
    onPlaySong: (Song) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: (String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onFavorite: ( String ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onShufflePlay: ( List<Song> ) -> Unit,
    onSortSongsByChange: ( SortSongsBy ) -> Unit,
    onSortSongsInReverseChange: ( Boolean ) -> Unit,
    onSortPathsByChange: ( SortPathsBy ) -> Unit,
    onSortPathsInReverseChange: ( Boolean ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
) {

    MediaSortBarScaffold(
        mediaSortBar = {
            TreeSongListMediaSortBar(
                songsCount = uiState.songsCount,
                sortPathsBy = uiState.sortPathsBy,
                sortPathsInReverse = uiState.sortPathsInReverse,
                sortSongsBy = uiState.sortSongsBy,
                sortSongsInReverse = uiState.sortSongsInReverse,
                language = uiState.language,
                onSortPathsByChange = onSortPathsByChange,
                onSortPathsInReverseChange = onSortPathsInReverseChange,
                onSongsSortByChange = onSortSongsByChange,
                onSortSongsInReverseChange = onSortSongsInReverseChange
            )
        }
    ) {
        when {
            uiState.tree.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector =Icons.Filled.MusicNote,
                        contentDescription = null
                    )
                }
            ) {
                Text( text = uiState.language.damnThisIsSoEmpty )
            }
            else -> TreeSongListContent(
                uiState = uiState,
                togglePath = togglePath,
                onPlaySong = onPlaySong,
                fallbackResourceId = fallbackResourceId,
                onAddToQueue = onAddToQueue,
                onShareSong = onShareSong,
                onPlayNext = onPlayNext,
                onViewArtist = onViewArtist,
                onViewAlbum = onViewAlbum,
                onFavorite = onFavorite,
                onAddSongsToPlaylist = onAddSongsToPlaylist,
                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                onCreatePlaylist = onCreatePlaylist,
                onGetPlaylists = onGetPlaylists,
                onGetSongsInPlaylist = onGetSongsInPlaylist,
                onShufflePlay = onShufflePlay,
                onDeleteSong = onDeleteSong,
            )
        }
    }
}

@Preview( showSystemUi = true )
@Composable
fun TreeSongListPreview() {
    TreeSongList(
        uiState = testTreeScreenUiState,
        togglePath = {},
        onPlaySong = {},
        fallbackResourceId = R.drawable.placeholder_light,
        onAddSongsToPlaylist = { _, _ -> },
        onSearchSongsMatchingQuery = { emptyList() },
        onCreatePlaylist = { _, _ -> },
        onFavorite = {},
        onShareSong = {},
        onViewArtist = {},
        onPlayNext = {},
        onViewAlbum = {},
        onAddToQueue = {},
        onGetPlaylists = { emptyList() },
        onGetSongsInPlaylist = { emptyList() },
        onShufflePlay = {},
        onSortSongsByChange = {},
        onSortSongsInReverseChange = {},
        onSortPathsByChange = {},
        onSortPathsInReverseChange = {},
        onDeleteSong = {}
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TreeSongListContent(
    uiState: TreeScreenUiState,
    @DrawableRes fallbackResourceId: Int,
    togglePath: ( String ) -> Unit,
    onPlaySong: ( Song ) -> Unit,
    onFavorite: ( String ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onShufflePlay: ( List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
) {

    val lazyListState = rememberLazyListState()

    LazyColumn (
        modifier = Modifier.drawScrollBar( lazyListState ),
        state = lazyListState
    ) {
        uiState.tree.forEach { ( directoryName, songsInDirectory ) ->
            val expanded = !uiState.disabledTreePaths.contains( directoryName )

            stickyHeader {
                var showOptionsMenu by remember { mutableStateOf( false ) }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background( MaterialTheme.colorScheme.surfaceColorAtElevation( 1.dp ) )
                        .clickable { togglePath( directoryName ) }
                        .padding(
                            start = 12.dp,
                            end = 8.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier
                            .size( 20.dp )
                        ,
                        imageVector = if ( expanded ) Icons.Filled.ExpandMore
                        else Icons.Filled.ChevronRight,
                        contentDescription = null
                    )
                    Spacer( modifier = Modifier.width( 4.dp ) )
                    Text(
                        modifier = Modifier.weight( 1f ),
                        text = directoryName,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    TreeSongListSongCardIconButton(
                        icon = { modifier ->
                            Icon(
                                modifier = modifier,
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showOptionsMenu = true
                        }
                    )
                }
                if ( showOptionsMenu ) {
                    ModalBottomSheet(
                        onDismissRequest = { showOptionsMenu = false
                        }
                    ) {
                        GenericOptionsBottomSheet(
                            headerTitle = uiState.language.path,
                            headerDescription = directoryName,
                            language = uiState.language,
                            fallbackResourceId = fallbackResourceId,
                            onDismissRequest = { showOptionsMenu = false },
                            onPlayNext = { songsInDirectory.forEach { onPlayNext( it ) } },
                            onAddToQueue = { songsInDirectory.forEach { onAddToQueue( it ) } },
                            onGetPlaylists = onGetPlaylists,
                            onGetSongsInPlaylist = onGetSongsInPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onCreatePlaylist = onCreatePlaylist,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onGetSongs = { songsInDirectory },
                            leadingBottomSheetMenuItem = { onDismissRequest ->
                                BottomSheetMenuItem(
                                    leadingIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
                                    label = uiState.language.shufflePlay
                                ) {
                                    onDismissRequest()
                                    onShufflePlay( songsInDirectory )
                                }
                            }
                        )
                    }
                }
            }
            if ( expanded ) {
                items( songsInDirectory ) { song ->
                    val isCurrentlyPlaying by remember( song, uiState.currentlyPlayingSongId ) {
                        derivedStateOf { song.id == uiState.currentlyPlayingSongId }
                    }
                    val isFavorite by remember( uiState.favoriteSongIds, song ) {
                        derivedStateOf { uiState.favoriteSongIds.contains( song.id ) }
                    }
                    SongCard(
                        language = uiState.language,
                        song = song,
                        isCurrentlyPlaying = isCurrentlyPlaying,
                        isFavorite = isFavorite,
                        playlistInfos = onGetPlaylists(),
                        fallbackResourceId = fallbackResourceId,
                        onClick = { onPlaySong(song) },
                        onFavorite = onFavorite,
                        onPlayNext = onPlayNext,
                        onAddToQueue = onAddToQueue,
                        onViewArtist = onViewArtist,
                        onViewAlbum = onViewAlbum,
                        onShareSong = onShareSong,
                        onAddSongsToPlaylist = onAddSongsToPlaylist,
                        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                        onCreatePlaylist = onCreatePlaylist,
                        onGetSongsInPlaylist = onGetSongsInPlaylist,
                        onGetSongAdditionalMetadata = {
                            uiState.songsAdditionalMetadataList.find { it.id == song.id }
                        },
                        onDeleteSong = onDeleteSong
                    )
                }
            }
            item {
                HorizontalDivider()
            }
        }
    }

}

@Preview( showSystemUi = true )
@Composable
fun TreeSongListContentPreview() {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = true
    ) {
        TreeSongListContent(
            uiState = testTreeScreenUiState,
            fallbackResourceId = R.drawable.placeholder_light,
            togglePath = {},
            onPlaySong = {},
            onPlayNext = {},
            onViewArtist = {},
            onFavorite = {},
            onViewAlbum = {},
            onShareSong = {},
            onAddToQueue = {},
            onAddSongsToPlaylist = { _, _ -> },
            onSearchSongsMatchingQuery = { emptyList() },
            onCreatePlaylist = { _, _ -> },
            onGetPlaylists = { emptyList() },
            onGetSongsInPlaylist = { emptyList() },
            onShufflePlay = {},
            onDeleteSong = {}
        )
    }
}

@Composable
fun TreeSongListSongCardIconButton(
    icon: @Composable ( Modifier ) -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        icon(
            Modifier
                .size(20.dp)
                .padding(start = 6.dp, top = 6.dp)
        )
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun TreeSongListMediaSortBar(
    songsCount: Int,
    sortPathsBy: SortPathsBy,
    sortPathsInReverse: Boolean,
    sortSongsBy: SortSongsBy,
    sortSongsInReverse: Boolean,
    language: Language,
    onSortPathsByChange: ( SortPathsBy ) -> Unit,
    onSortPathsInReverseChange: ( Boolean ) -> Unit,
    onSongsSortByChange: ( SortSongsBy ) -> Unit,
    onSortSongsInReverseChange: ( Boolean ) -> Unit,
) {
    val currentTextStyle = MaterialTheme.typography.bodySmall.run {
        copy( color = MaterialTheme.colorScheme.onSurface )
    }
    var showSortMenu by remember { mutableStateOf( false ) }

    Row (
        modifier = Modifier
            .padding(16.dp, 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { showSortMenu = true }
        ) {
            Row (
                modifier = Modifier.padding( end = 2.dp ),
                horizontalArrangement = Arrangement.spacedBy( 8.dp ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size( 12.dp ),
                    imageVector = if ( sortPathsInReverse ) Icons.Filled.ArrowUpward
                    else Icons.Filled.ArrowDownward,
                    contentDescription = null
                )
                Text( text = sortPathsBy.sortPathsByLabel( language ) )
                VerticalDivider(
                    modifier = Modifier
                        .size(9.dp, 12.dp)
                        .padding(4.dp, 0.dp)
                )
                Icon(
                    modifier = Modifier.size( 12.dp ),
                    imageVector = if ( sortSongsInReverse ) Icons.Filled.ArrowUpward
                    else Icons.Filled.ArrowDownward,
                    contentDescription = null
                )
                Text( text = sortSongsBy.sortSongsByLabel( language ) )
            }
        }
        Text(
            modifier = Modifier.padding( 16.dp, 0.dp ),
            text = language.xSongs( songsCount.toString() ),
            style = currentTextStyle,
        )
        if ( showSortMenu ) {
            ModalBottomSheet(
                onDismissRequest = { showSortMenu = false }
            ) {
                TreeScreenSortOptionsBottomMenu(
                    sortPathsBy = sortPathsBy,
                    sortPathsInReverse = sortPathsInReverse,
                    sortSongsBy = sortSongsBy,
                    sortSongsInReverse = sortSongsInReverse,
                    language = language,
                    onSortPathsByChange = onSortPathsByChange,
                    onSortPathsInReverseChange = onSortPathsInReverseChange,
                    onSortSongsByChange = onSongsSortByChange,
                    onSortSongsReverseChange = onSortSongsInReverseChange,
                    onDismissRequest = { showSortMenu = false }
                )
            }
        }
    }
}

@Composable
private fun TreeScreenSortOptionsBottomMenu(
    sortPathsBy: SortPathsBy,
    sortPathsInReverse: Boolean,
    sortSongsBy: SortSongsBy,
    sortSongsInReverse: Boolean,
    language: Language,
    onSortPathsByChange: ( SortPathsBy ) -> Unit,
    onSortPathsInReverseChange: ( Boolean ) -> Unit,
    onSortSongsByChange: ( SortSongsBy ) -> Unit,
    onSortSongsReverseChange: ( Boolean ) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Row {
        Column(
            modifier = Modifier.weight( 0.9f )
        ) {
            Text(
                modifier = Modifier.padding( 16.dp, 8.dp ),
                text = language.folders,
            )
            SortPathsBy.entries.forEach { sortBy ->
                TreeSongListMediaSortBarDropdownMenuItem(
                    selected = sortPathsBy == sortBy,
                    reversed = sortPathsInReverse,
                    text = {
                        Text( text = sortBy.sortPathsByLabel( language ) )
                    },
                    onClick = {
                        when ( sortPathsBy ) {
                            sortBy -> onSortPathsInReverseChange( !sortPathsInReverse )
                            else -> onSortPathsByChange( sortBy )
                        }
                        onDismissRequest()
                    }
                )
            }
        }
        Column (
            modifier = Modifier.weight( 1f )
        ) {
            Text(
                modifier = Modifier.padding( 16.dp, 8.dp ),
                text = language.songs,
            )
            SortSongsBy.entries.forEach { sortBy ->
                TreeSongListMediaSortBarDropdownMenuItem(
                    selected = sortSongsBy == sortBy,
                    reversed = sortSongsInReverse,
                    text = {
                        Text( text = sortBy.sortSongsByLabel( language ) )
                    },
                    onClick = {
                        when ( sortSongsBy ) {
                            sortBy -> onSortSongsReverseChange( !sortSongsInReverse )
                            else -> onSortSongsByChange( sortBy )
                        }
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Preview( showSystemUi = true )
@Composable
fun TreeScreenSortOptionsBottomMenuPreview() {
    TreeScreenSortOptionsBottomMenu(
        language = English,
        sortPathsBy = SortPathsBy.NAME,
        sortPathsInReverse = false,
        sortSongsBy = SettingsDefaults.sortSongsBy,
        sortSongsInReverse = false,
        onSortSongsByChange = {},
        onSortPathsByChange = {},
        onSortSongsReverseChange = {},
        onSortPathsInReverseChange = {},
        onDismissRequest = {}
    )
}

@Preview( showBackground = true )
@Composable
fun TreeSongListMediaSortBarPreview() {
    TreeSongListMediaSortBar(
        songsCount = 50,
        sortPathsBy = SortPathsBy.NAME,
        sortPathsInReverse = false,
        sortSongsBy = SortSongsBy.CUSTOM,
        language = English,
        sortSongsInReverse = false,
        onSortPathsByChange = {},
        onSortPathsInReverseChange = {},
        onSongsSortByChange = {},
        onSortSongsInReverseChange = {}
    )
}

@Composable
private fun TreeSongListMediaSortBarDropdownMenuItem(
    selected: Boolean,
    reversed: Boolean,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        contentPadding = MenuDefaults.DropdownMenuItemContentPadding.run {
            val horizontalPadding = calculateLeftPadding( LayoutDirection.Ltr )
            PaddingValues(
                start = horizontalPadding.div( 2 ),
                end = horizontalPadding.times( 4 )
            )
        },
        leadingIcon = {
            when {
                selected -> IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        imageVector = if ( reversed ) Icons.Filled.ArrowCircleUp
                        else Icons.Filled.ArrowCircleDown,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
                else -> RadioButton(
                    selected = false,
                    onClick = onClick
                )
            }
        },
        text = text,
        onClick = onClick
    )
}