package com.squad.musicmatters.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.squad.musicmatters.R
import com.squad.musicmatters.core.media.media.extensions.formatMilliseconds
import com.squad.musicmatters.core.data.utils.VersionUtils
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
import com.squad.musicmatters.utils.ScreenOrientation

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun SongCard(
    language: Language,
    song: Song,
    isCurrentlyPlaying: Boolean,
    isFavorite: Boolean,
    playlistInfos: List<PlaylistInfo>,
    @DrawableRes fallbackResourceId: Int,
    onClick: () -> Unit,
    onFavorite: ( String ) -> Unit,
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors( containerColor = Color.Transparent ),
        onClick = onClick
    ) {
        Box( modifier = Modifier.padding( 12.dp, 12.dp, 4.dp, 12.dp ) ) {
            Row( verticalAlignment = Alignment.CenterVertically ) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder( LocalContext.current ).apply {
                            data( song.artworkUri )
                            placeholder( fallbackResourceId )
                            fallback( fallbackResourceId )
                            error( fallbackResourceId )
                            crossfade( true )
                        }.build(),
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentDescription = null
                    )
                }
                Spacer( modifier = Modifier.width( 16.dp ) )
                Column( modifier = Modifier.weight( 1f ) ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
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
                            color = LocalTextStyle.current.color.copy(
                                alpha = 0.5f
                            )
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer( modifier = Modifier.width( 15.dp ) )
                Row {
                    if ( isFavorite ) {
                        IconButton(
                            onClick = { onFavorite( song.id ) }
                        ) {
                            Icon(
                                modifier = Modifier.size( 24.dp ),
                                imageVector = Icons.Filled.Favorite,
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
                                    fallbackResourceId = fallbackResourceId,
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
    @DrawableRes fallbackResourceId: Int,
    onFavorite: ( String ) -> Unit,
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
        headerImage = ImageRequest.Builder( LocalContext.current ).apply {
            data( song.artworkUri )
            placeholder( fallbackResourceId )
            fallback( fallbackResourceId )
            error( fallbackResourceId )
            crossfade( true )
        }.build(),
        headerTitle = song.title,
        titleIsHighlighted = isCurrentlyPlaying,
        headerDescription = song.artists.joinToString(),
        language = language,
        fallbackResourceId = fallbackResourceId,
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
                onFavorite( song.id )
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

@Composable
fun QueueSongCard(
    modifier: Modifier = Modifier,
    language: Language,
    song: Song,
    isCurrentlyPlaying: Boolean,
    isFavorite: Boolean,
    playlistInfos: List<PlaylistInfo>,
    @DrawableRes fallbackResourceId: Int,
    onClick: () -> Unit,
    onFavorite: ( String ) -> Unit,
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
    onGetSongAdditionalMetadata: () -> SongAdditionalMetadataInfo?
) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = modifier,
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
            fallbackResourceId = fallbackResourceId,
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
            onDeleteSong = onDeleteSong
        )
    }
}

//@Preview( showBackground = true )
//@Composable
//fun SongOptionsBottomSheetContentPreview() {
//    MusicMattersTheme(
//        fontName = SupportedFonts.ProductSans.name,
//        useMaterialYou = true,
//        fontScale = 1.0f,
//        themeMode = ThemeMode.LIGHT,
//        primaryColorName = PrimaryThemeColors.Blue.name
//    ) {
//        SongOptionsBottomSheetMenu(
//            language = English,
//            song = PreviewParameterData.first(),
//            isFavorite = true,
//            isCurrentlyPlaying = true,
//            fallbackResourceId = R.drawable.placeholder_light,
//            onFavorite = {},
//            onAddToQueue = {},
//            onPlayNext = { /*TODO*/ },
//            onViewArtist = {},
//            onViewAlbum = {},
//            onShareSong = {},
//            onShowSongDetails = {},
//            onAddSongsToPlaylist = { _, _ -> },
//            onCreatePlaylist = { _, _ -> },
//            onGetPlaylists = { emptyList() },
//            onGetSongsInPlaylist = { emptyList() },
//            onSearchSongsMatchingQuery = { emptyList() },
//            onDelete = {},
//            onDismissRequest = {}
//        )
//    }
//
//}
//
//@Preview( showBackground = true )
//@Composable
//fun SongCardPreview() {
//    MusicMattersTheme(
//        fontName = SupportedFonts.ProductSans.name,
//        useMaterialYou = true,
//        fontScale = 1.0f,
//        themeMode = ThemeMode.LIGHT,
//        primaryColorName = PrimaryThemeColors.Blue.name
//    ) {
//        SongCard(
//            language = English,
//            song = testSongs.first(),
//            isCurrentlyPlaying = true,
//            isFavorite = true,
//            playlistInfos = emptyList(),
//            fallbackResourceId = R.drawable.placeholder_light,
//            onClick = {},
//            onFavorite = {},
//            onPlayNext = {},
//            onAddToQueue = {},
//            onViewArtist = {},
//            onViewAlbum = {},
//            onShareSong = {},
//            onGetSongsInPlaylist = { emptyList() },
//            onAddSongsToPlaylist = { _, _ -> },
//            onSearchSongsMatchingQuery = { emptyList() },
//            onCreatePlaylist = { _, _ -> },
//            onGetSongAdditionalMetadata = { null },
//            onDeleteSong = {}
//        )
//    }
//}
//
//@Preview( showBackground = true )
//@Composable
//fun QueueSongCardPreview() {
//    MusicMattersTheme(
//        fontName = SupportedFonts.ProductSans.name,
//        useMaterialYou = true,
//        fontScale = 1.0f,
//        themeMode = ThemeMode.LIGHT,
//        primaryColorName = PrimaryThemeColors.Blue.name
//    ) {
//        QueueSongCard(
//            language = English,
//            song = testSongs.first(),
//            isCurrentlyPlaying = true,
//            isFavorite = true,
//            playlistInfos = emptyList(),
//            fallbackResourceId = R.drawable.placeholder_light,
//            onClick = {},
//            onFavorite = {},
//            onPlayNext = {},
//            onAddToQueue = {},
//            onViewArtist = {},
//            onViewAlbum = {},
//            onShareSong = {},
//            onGetSongsInPlaylist = { emptyList() },
//            onDragHandleClick = {},
//            onAddSongsToPlaylist = { _, _ -> },
//            onSearchSongsMatchingQuery = { emptyList() },
//            onCreatePlaylist = { _, _ -> },
//            onDeleteSong = {},
//            onGetSongAdditionalMetadata = { null }
//        )
//    }
//}