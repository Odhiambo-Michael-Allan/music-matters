package com.squad.musicmatters.core.ui

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ThumbUpAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.media.media.extensions.formatMilliseconds
import com.squad.musicmatters.core.data.utils.VersionUtils
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.GoogleRed
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.dialog.DeleteSongDialog
import com.squad.musicmatters.core.ui.dialog.SongDetailsDialog

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun SongCard(
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
    onGetSongsInPlaylist: ( PlaylistInfo ) -> List<Song>,
    onAddSongsToPlaylist: ( PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetSongAdditionalMetadata: () -> SongAdditionalMetadataInfo?,
    onDeleteSong: ( Song ) -> Unit,
) {

    var showSongOptionsBottomSheet by remember { mutableStateOf( false ) }
    var showSongDetailsDialog by remember { mutableStateOf( false ) }
    var showDeleteSongDialog by remember { mutableStateOf( false ) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors( containerColor = Color.Transparent ),
        onClick = onClick
    ) {
        Box( modifier = Modifier.padding( 12.dp, 4.dp, 4.dp, 4.dp ) ) {
            Row( verticalAlignment = Alignment.CenterVertically ) {
                Box {
                    DynamicAsyncImage(
                        imageUri = song.artworkUri?.toUri(),
                        contentDescription = song.title,
                        modifier = Modifier
                            .size( 45.dp )
                            .clip( RoundedCornerShape( 10.dp ) )
                    )
                }
                Spacer( modifier = Modifier.width( 16.dp ) )
                Column( modifier = Modifier.weight( 1f ) ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = when {
                                isCurrentlyPlaying -> MaterialTheme.colorScheme.primary
                                else -> LocalTextStyle.current.color
                            },
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artists.joinToString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer( modifier = Modifier.width( 15.dp ) )
                Row {
                    if ( isFavorite ) {
                        IconButton(
                            onClick = {
                                onFavorite( song.id, false )
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size( 24.dp ),
                                imageVector = Icons.Rounded.ThumbUpAlt,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }
                    }
                    IconButton(
                        onClick = { showSongOptionsBottomSheet = !showSongOptionsBottomSheet }
                    ) {
                        Icon(
                            modifier = Modifier.size( 24.dp ),
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                        if ( showSongOptionsBottomSheet ) {
                            ModalBottomSheet(
                                modifier = if ( ScreenOrientation.fromConfiguration(
                                        LocalConfiguration.current ).isLandscape
                                ) Modifier.padding( start = 50.dp ) else Modifier,
                                onDismissRequest = {
                                    showSongOptionsBottomSheet = false
                                }
                            ) {
                                SongOptionsBottomSheetMenu(
                                    language = language,
                                    song = song,
                                    isFavorite = isFavorite,
                                    isCurrentlyPlaying = isCurrentlyPlaying,
                                    onFavorite = onFavorite,
                                    onAddToQueue = onAddToQueue,
                                    onPlayNext = onPlayNext,
                                    onViewArtist = onViewArtist,
                                    onViewAlbum = onViewAlbum,
                                    onShareSong = onShareSong,
                                    onShowSongDetails = { showSongDetailsDialog = true },
                                    onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                                    onCreatePlaylist = onCreatePlaylist,
                                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                                    onGetSongsInPlaylist = onGetSongsInPlaylist,
                                    onGetPlaylists = { playlistInfos },
                                    onDelete = {
                                        if ( !VersionUtils.isQandAbove() ) {
                                            showDeleteSongDialog = true
                                        } else {
                                            onDeleteSong( it )
                                        }
                                    },
                                    onDismissRequest = {
                                        showSongOptionsBottomSheet = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            if ( showSongDetailsDialog ) {
                SongDetailsDialog(
                    song = song,
                    language = language,
                    durationFormatter = { it.formatMilliseconds() },
                    isLoadingSongAdditionalMetadata = onGetSongAdditionalMetadata() == null,
                    onGetSongAdditionalMetadata = onGetSongAdditionalMetadata
                ) {
                    showSongDetailsDialog = false
                }
            }
            if ( showDeleteSongDialog ) {
                DeleteSongDialog(
                    song = song,
                    language = language,
                    onDelete = { onDeleteSong( song ) }
                ) {
                    showDeleteSongDialog = false
                }
            }
        }
    }
}

@Composable
fun SongOptionsBottomSheetMenu(
    language: Language,
    song: Song,
    isFavorite: Boolean,
    isCurrentlyPlaying: Boolean,
    onFavorite: ( String, Boolean ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onShowSongDetails: () -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onSearchSongsMatchingQuery: (String ) -> List<Song>,
    onCreatePlaylist: (String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onDelete: ( Song ) -> Unit,
    onDismissRequest: () -> Unit,
) {
    GenericOptionsBottomSheet(
        headerImageUri = song.artworkUri?.toUri(),
        headerTitle = song.title,
        titleIsHighlighted = isCurrentlyPlaying,
        headerDescription = song.artists.joinToString(),
        language = language,
        onDismissRequest = onDismissRequest,
        onPlayNext = { onPlayNext( song ) },
        onAddToQueue = { onAddToQueue( song ) },
        onGetPlaylists = onGetPlaylists,
        onGetSongsInPlaylist = onGetSongsInPlaylist,
        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
        onCreatePlaylist = onCreatePlaylist,
        onAddSongsToPlaylist = onAddSongsToPlaylist,
        onGetSongs = { listOf( song ) },
        leadingBottomSheetMenuItem = {
            BottomSheetMenuItem(
                leadingIcon = if ( isFavorite ) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                label = language.favorite
            ) {
                onFavorite( song.id, !isFavorite )
                onDismissRequest()
            }
        },
        trailingBottomSheetMenuItems = {
            song.artists.forEach {
                BottomSheetMenuItem(
                    leadingIcon = Icons.Rounded.Person,
                    label = "${language.viewArtist}: $it"
                ) {
                    onDismissRequest()
                    onViewArtist( it )
                }
            }
            song.albumTitle?.let {
                BottomSheetMenuItem(
                    leadingIcon = Icons.Filled.Album,
                    label = language.viewAlbum
                ) {
                    onDismissRequest()
                    onViewAlbum( it )
                }
            }
            BottomSheetMenuItem(
                leadingIcon = Icons.Rounded.Share,
                label = language.shareSong
            ) {
                onDismissRequest()
                onShareSong( song.mediaUri.toUri() )
            }
            BottomSheetMenuItem(
                leadingIcon = Icons.Rounded.Info,
                label = language.details
            ) {
                onDismissRequest()
                onShowSongDetails()
            }
            BottomSheetMenuItem(
                leadingIcon = Icons.Rounded.Delete,
                leadingIconTint = GoogleRed,
                label = language.delete,
            ) {
                onDismissRequest()
                onDelete( song )
            }
        }
    )
}

@Preview( showBackground = true )
@Composable
private fun SongOptionsBottomSheetContentPreview() {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = 1.0f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        SongOptionsBottomSheetMenu(
            language = English,
            song = PreviewParameterData.songs.first(),
            isFavorite = true,
            isCurrentlyPlaying = true,
            onFavorite = { _, _ -> },
            onAddToQueue = {},
            onPlayNext = { /*TODO*/ },
            onViewArtist = {},
            onViewAlbum = {},
            onShareSong = {},
            onShowSongDetails = {},
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onGetPlaylists = { emptyList() },
            onGetSongsInPlaylist = { emptyList() },
            onSearchSongsMatchingQuery = { emptyList() },
            onDelete = {},
            onDismissRequest = {}
        )
    }
}

@Preview( showBackground = true )
@Composable
private fun SongCardPreview() {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = 1.0f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        SongCard(
            language = English,
            song = PreviewParameterData.songs.first(),
            isCurrentlyPlaying = true,
            isFavorite = true,
            playlistInfos = emptyList(),
            onClick = {},
            onFavorite = { _, _ -> },
            onPlayNext = {},
            onAddToQueue = {},
            onViewArtist = {},
            onViewAlbum = {},
            onShareSong = {},
            onGetSongsInPlaylist = { emptyList() },
            onAddSongsToPlaylist = { _, _ -> },
            onSearchSongsMatchingQuery = { emptyList() },
            onCreatePlaylist = { _, _ -> },
            onGetSongAdditionalMetadata = { null },
            onDeleteSong = {}
        )
    }
}


