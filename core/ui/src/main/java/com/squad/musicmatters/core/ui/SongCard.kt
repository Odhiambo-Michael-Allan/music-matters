package com.squad.musicmatters.core.ui

import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.media.media.extensions.formatMilliseconds
import com.squad.musicmatters.core.data.utils.VersionUtils
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.GoogleRed
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.dialog.DeleteSongDialog
import com.squad.musicmatters.core.ui.dialog.SongDetailsDialog

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun SongCard(
    modifier: Modifier = Modifier,
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
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    var showSongOptionsBottomSheet by remember { mutableStateOf( false ) }
    var showSongDetailsDialog by remember { mutableStateOf( false ) }
    var showDeleteSongDialog by remember { mutableStateOf( false ) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors( containerColor = Color.Transparent ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding( 12.dp, 4.dp, 4.dp, 4.dp )
        ) {
            Row( verticalAlignment = Alignment.CenterVertically ) {
                Box {
                    DynamicAsyncImage(
                        imageUri = song.artworkUri?.toUri(),
                        contentDescription = song.title,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(10.dp))
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
                                onFavorite( song, false )
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
                            imageVector = MusicMattersIcons.MoreVertical,
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
                                    song = song,
                                    isFavorite = isFavorite,
                                    isCurrentlyPlaying = isCurrentlyPlaying,
                                    playlists = playlists,
                                    onFavorite = onFavorite,
                                    onAddToQueue = onAddToQueue,
                                    onPlayNext = onPlayNext,
                                    onViewArtist = onViewArtist,
                                    onViewAlbum = onViewAlbum,
                                    onShareSong = onShareSong,
                                    onShowSongDetails = { showSongDetailsDialog = true },
                                    onCreatePlaylist = onCreatePlaylist,
                                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                                    onDelete = {
                                        if ( !VersionUtils.isQandAbove() ) {
                                            showDeleteSongDialog = true
                                        } else {
                                            onDeleteSong( it )
                                        }
                                    },
                                    onDismissRequest = {
                                        showSongOptionsBottomSheet = false
                                    },
                                    onShowSnackBar = onShowSnackBar,
                                )
                            }
                        }
                    }
                }
            }
            if ( showSongDetailsDialog ) {
                SongDetailsDialog(
                    song = song,
                    durationFormatter = { it.formatMilliseconds() },
                    metadata = songAdditionalMetadata,
                ) {
                    showSongDetailsDialog = false
                }
            }
            if ( showDeleteSongDialog ) {
                DeleteSongDialog(
                    song = song,
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
    song: Song,
    isFavorite: Boolean,
    isCurrentlyPlaying: Boolean,
    playlists: List<Playlist>,
    onFavorite: ( Song, Boolean ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onShowSongDetails: () -> Unit,
    onCreatePlaylist: (String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onDelete: ( Song ) -> Unit,
    onDismissRequest: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {
    val context = LocalContext.current

    GenericOptionsBottomSheet(
        headerImageUri = song.artworkUri?.toUri(),
        headerTitle = song.title,
        titleIsHighlighted = isCurrentlyPlaying,
        headerDescription = song.artists.joinToString(),
        playlists = playlists,
        onDismissRequest = onDismissRequest,
        onPlayNext = { onPlayNext( song ) },
        onAddToQueue = { onAddToQueue( song ) },
        onCreatePlaylist = onCreatePlaylist,
        onAddSongsToPlaylist = onAddSongsToPlaylist,
        onGetSongs = { listOf( song ) },
        leadingBottomSheetMenuItem = {
            BottomSheetMenuItem(
                leadingIcon = if ( isFavorite ) {
                    MusicMattersIcons.Favorite
                } else {
                    MusicMattersIcons.FavoriteBorder
                },
                label = stringResource( id = R.string.core_i8n_favorite )
            ) {
                val feedback = context.getString(
                    if ( isFavorite ) R.string.core_i8n_removed_song_from_favorites
                    else R.string.core_i8n_added_song_to_favorites
                )
                onFavorite( song, !isFavorite )
                onDismissRequest()
                onShowSnackBar( feedback )
            }
        },
        onShowSnackBar = onShowSnackBar,
        trailingBottomSheetMenuItems = {
            song.artists.forEach {
                BottomSheetMenuItem(
                    leadingIcon = MusicMattersIcons.Artist,
                    label = stringResource( id = R.string.core_i8n_view_artist, it )
                ) {
                    onDismissRequest()
                    onViewArtist( it )
                }
            }
            song.albumTitle?.let {
                BottomSheetMenuItem(
                    leadingIcon = MusicMattersIcons.Album,
                    label = stringResource( id = R.string.core_i8n_view_album, it )
                ) {
                    onDismissRequest()
                    onViewAlbum( it )
                }
            }
            BottomSheetMenuItem(
                leadingIcon = MusicMattersIcons.Share,
                label = stringResource( id = R.string.core_i8n_share_song )
            ) {
                onDismissRequest()
                onShareSong( song.mediaUri.toUri() )
            }
            BottomSheetMenuItem(
                leadingIcon = MusicMattersIcons.SongDetails,
                label = stringResource( id = R.string.core_i8n_details )
            ) {
                onDismissRequest()
                onShowSongDetails()
            }
            BottomSheetMenuItem(
                leadingIcon = MusicMattersIcons.Delete,
                leadingIconTint = GoogleRed,
                label = stringResource( id = R.string.core_i8n_delete ),
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
        fontName = SupportedFonts.GoogleSans.name,
        useMaterialYou = true,
        fontScale = 1.0f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        SongOptionsBottomSheetMenu(
            song = PreviewParameterData.songs.first(),
            isFavorite = false,
            isCurrentlyPlaying = true,
            playlists = emptyList(),
            onFavorite = { _, _ -> },
            onAddToQueue = {},
            onPlayNext = { /*TODO*/ },
            onViewArtist = {},
            onViewAlbum = {},
            onShareSong = {},
            onShowSongDetails = {},
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onDelete = {},
            onDismissRequest = {},
            onShowSnackBar = {},
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
            song = PreviewParameterData.songs.first(),
            isCurrentlyPlaying = true,
            isFavorite = true,
            playlists = emptyList(),
            songAdditionalMetadata = null,
            onClick = {},
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
        )
    }
}


