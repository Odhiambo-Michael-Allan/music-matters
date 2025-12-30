package com.squad.musicmatters.core.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.GenericCard
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.PreviewParameterData
import com.squad.musicmatters.core.ui.SubtleCaptionText

@Composable
internal fun AddSongToPlaylistBottomSheet(
    songs: List<Song>,
    playlists: List<PlaylistInfo>,
    language: Language,
    onGetSongsInPlaylist: ( PlaylistInfo ) -> List<Song>,
    onAddDisplayedSongsToPlaylist: ( PlaylistInfo ) -> Unit,
    onCreateNewPlaylist: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, 4.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = language.addToPlaylist,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        HorizontalDivider( thickness = 1.dp )
        when {
            playlists.isEmpty() -> SubtleCaptionText(
                modifier = Modifier.weight( 1f ),
                text = language.noInAppPlaylistsFound
            )
            else -> {
                LazyColumn (
                    modifier = Modifier.weight( 1f ),
                    contentPadding = PaddingValues( bottom = 4.dp )
                ) {
                    items( playlists ) { playlist ->
                        GenericCard(
                            imageUri = onGetSongsInPlaylist( playlist )
                                .firstOrNull { it.artworkUri != null }?.artworkUri?.toUri(),
                            title = {
                                Text(
                                    text = playlist.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            imageLabel = {
                                if ( songs.size == 1  && onGetSongsInPlaylist( playlist ).contains( songs.first() ) ) {
                                    Icon(
                                        modifier = Modifier.size( 16.dp ),
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null
                                    )
                                }
                            },
                            subtitle = {
                                Text(
                                    text = "${onGetSongsInPlaylist(playlist).size} songs",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                onDismissRequest()
                                onAddDisplayedSongsToPlaylist( playlist )
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
                    text = language.newPlaylist,
                    style = MaterialTheme.typography.bodySmall.copy(
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
        AddSongToPlaylistBottomSheet(
            songs = previewData.songs,
            playlists = previewData.playlists,
            onGetSongsInPlaylist = { previewData.songs },
            onCreateNewPlaylist = {},
            onDismissRequest = {},
            language = English,
            onAddDisplayedSongsToPlaylist = {}
        )
    }
}