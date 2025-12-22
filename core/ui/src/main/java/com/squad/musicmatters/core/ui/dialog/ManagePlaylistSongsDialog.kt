package com.squad.musicmatters.core.ui.dialog

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import coil.request.ImageRequest
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.GenericCard
import com.squad.musicmatters.core.ui.PreviewParameterData
import com.squad.musicmatters.core.ui.R
import com.squad.musicmatters.core.ui.SubtleCaptionText

@SuppressLint( "UnusedBoxWithConstraintsScope" )
@Composable
fun ManagePlaylistSongsDialog(
    currentlySelectedSongs: List<Song>,
    language: Language,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onDismissRequest: ( List<Song> ) -> Unit,
    onCancel: () -> Unit,
) {

    var searchQuery by remember { mutableStateOf( "" ) }
    val songsMatchingSearchQuery by remember( searchQuery ) {
        derivedStateOf {
            onSearchSongsMatchingQuery( searchQuery )
        }
    }
    val selectedSongs = remember { currentlySelectedSongs.toMutableStateList() }

    Dialog(
        onDismissRequest = { onDismissRequest( selectedSongs ) }
    ) {
        BoxWithConstraints {
            val dialogWidth = maxWidth * 0.85f
            val dialogHeight = maxHeight * 0.65f

            Card (
                modifier = Modifier
                    .width(dialogWidth)
                    .height(dialogHeight),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape( 16.dp )
            ) {
                Column {
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = onCancel
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                        Row (
                            modifier = Modifier.weight( 1f ),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding( 12.dp ),
                                text = language.manageSongs,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        IconButton(
                            onClick = { onDismissRequest( selectedSongs ) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    }
                    OutlinedTextField(
                        modifier = Modifier.padding( 8.dp ),
                        singleLine = true,
                        value = searchQuery,
                        placeholder = {
                            Text( text = language.searchYourMusic )
                        },
                        onValueChange = {
                            searchQuery = it
                        },
                        shape = RoundedCornerShape( 32.dp )
                    )
                    when {
                        songsMatchingSearchQuery.isEmpty() -> SubtleCaptionText( text = language.damnThisIsSoEmpty )
                        else -> {
                            LazyColumn (
                                contentPadding = PaddingValues( 0.dp, 4.dp )
                            ) {
                                items( songsMatchingSearchQuery ) {
                                    GenericCard(
                                        imageUri = it.artworkUri?.toUri(),
                                        title = {
                                            Text( text = it.title )
                                        },
                                        subtitle = {
                                            Text(text = it.artists.joinToString() )
                                        },
                                        imageLabel = {
                                            when {
                                                selectedSongs.contains( it ) ->
                                                    Icon(
                                                        modifier = Modifier.size( 16.dp ),
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null
                                                    )
                                            }
                                        },
                                        onClick = {
                                            when {
                                                selectedSongs.contains( it ) -> selectedSongs.remove( it )
                                                else -> selectedSongs.add( it )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview( showSystemUi = true )
@Composable
fun ManagePlaylistSongsDialogPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        ManagePlaylistSongsDialog(
            currentlySelectedSongs = PreviewParameterData.songs,
            language = English,
            onSearchSongsMatchingQuery = { PreviewParameterData.songs },
            onDismissRequest = {},
            onCancel = {}
        )
    }
}