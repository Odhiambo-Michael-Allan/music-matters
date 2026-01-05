package com.squad.musicmatters.core.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.GenericCard
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.R
import com.squad.musicmatters.core.ui.SubtleCaptionText

import com.squad.musicMatters.core.i8n.R as i8nR

@Composable
internal fun AddSongsToPlaylistBottomSheet(
    songsToAdd: List<Song>,
    playlists: List<Playlist>,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreateNewPlaylist: () -> Unit,
    onDismissRequest: ( String ) -> Unit,
) {
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding( 8.dp, 4.dp )
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding( 8.dp ),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource( id = i8nR.string.core_i8n_add_to_playlist ),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        HorizontalDivider( thickness = 1.dp )
        Spacer( modifier = Modifier.height( 8.dp ) )
        when {
            playlists.isEmpty() -> SubtleCaptionText(
                modifier = Modifier.weight( 1f ),
                text = stringResource( id = i8nR.string.core_i8n_no_playlists_found )
            )
            else -> {
                LazyColumn (
                    modifier = Modifier.weight( 1f ),
                    contentPadding = PaddingValues( bottom = 4.dp )
                ) {
                    items( playlists ) { playlist ->
                        GenericCard(
                            imageUri = playlist.artworkUri?.toUri(),
                            title = {
                                Text(
                                    text = if ( playlist.id == FAVORITES_PLAYLIST_ID ) {
                                        stringResource( id = i8nR.string.core_i8n_favorites )
                                    } else {
                                        playlist.title
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            imageLabel = {
                                if ( songsToAdd.size == 1
                                    && playlist.songIds.contains( songsToAdd.first().id ) )
                                {
                                    Icon(
                                        modifier = Modifier.size( 16.dp ),
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null
                                    )
                                }
                            },
                            subtitle = {
                                Text(
                                    text = "${playlist.songIds.size} songs",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                val playlistTitle = if ( playlist.id == FAVORITES_PLAYLIST_ID ) {
                                    context.getString( i8nR.string.core_i8n_favorites )
                                } else {
                                    playlist.title
                                }
                                val message = if ( songsToAdd.size > 1 ) {
                                    context.getString(
                                        i8nR.string.core_i8n_added_n_songs_to_playlist,
                                        songsToAdd.size,
                                        playlistTitle
                                    )
                                } else {
                                    context.getString(
                                        i8nR.string.core_i8n_added_song_to_playlist,
                                        playlistTitle
                                    )
                                }
                                onAddSongsToPlaylist( playlist, songsToAdd )
                                onDismissRequest( message )
                            }
                        )
                    }
                }
            }
        }
        Row (
            modifier = Modifier.padding( 8.dp ),
        ) {
            TextButton(
                onClick = onCreateNewPlaylist
            ) {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_new_playlist ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddToPlaylistBottomSheetPreview(
    @PreviewParameter(MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = 1.0f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        AddSongsToPlaylistBottomSheet(
            songsToAdd = previewData.songs,
            playlists = previewData.playlists,
            onCreateNewPlaylist = {},
            onDismissRequest = {},
            onAddSongsToPlaylist = { _, _ -> }
        )
    }
}