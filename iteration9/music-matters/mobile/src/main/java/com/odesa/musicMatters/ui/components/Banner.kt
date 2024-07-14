package com.odesa.musicMatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.utils.ScreenOrientation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Banner(
    imageRequest: ImageRequest,
    bottomSheetHeaderTitle: String,
    bottomSheetHeaderDescription: String,
    language: Language,
    @DrawableRes fallbackResourceId: Int,
    onShufflePlay: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onGetSongs: () -> List<Song>,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    additionalBottomSheetMenuItems: ( @Composable ( () -> Unit ) -> Unit )? = null,
    content: @Composable () -> Unit
) {
    val defaultHorizontalPadding = 20.dp
    var showBottomSheetMenu by remember { mutableStateOf( false ) }

    BoxWithConstraints {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    when (ScreenOrientation.fromConfiguration(LocalConfiguration.current)) {
                        ScreenOrientation.POTRAIT -> maxWidth.times(0.7f)
                        ScreenOrientation.LANDSCAPE -> maxWidth.times(0.25f)
                    }
                ),
            model = imageRequest,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Row (
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        1f to MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.7f
                        )
                    )
                )
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .padding(defaultHorizontalPadding, 32.dp, defaultHorizontalPadding, 12.dp)
                    .weight(1f)
            ) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold )
                ) {
                    content()
                }
            }
            IconButton(
                onClick = {
                    showBottomSheetMenu = !showBottomSheetMenu
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
        }

        if ( showBottomSheetMenu ) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheetMenu = false }
            ) {
                GenericOptionsBottomSheet(
                    headerImage = imageRequest,
                    headerTitle = bottomSheetHeaderTitle,
                    headerDescription = bottomSheetHeaderDescription,
                    language = language,
                    fallbackResourceId = fallbackResourceId,
                    onDismissRequest = { showBottomSheetMenu = false },
                    onPlayNext = onPlayNext,
                    onAddToQueue = onAddToQueue,
                    onGetSongs = onGetSongs,
                    onCreatePlaylist = onCreatePlaylist,
                    onGetPlaylists = onGetPlaylists,
                    onGetSongsInPlaylist = onGetSongsInPlaylist,
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                    trailingBottomSheetMenuItems = additionalBottomSheetMenuItems,
                    leadingBottomSheetMenuItem = { onDismissRequest ->
                        BottomSheetMenuItem(
                            leadingIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
                            label = language.shufflePlay
                        ) {
                            onDismissRequest()
                            onShufflePlay()
                        }
                    }
                )
            }
        }
    }
}