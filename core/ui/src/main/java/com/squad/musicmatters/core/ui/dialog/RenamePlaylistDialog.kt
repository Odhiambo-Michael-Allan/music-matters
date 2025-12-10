package com.squad.musicmatters.core.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData

@Composable
fun RenamePlaylistDialog(
    playlistTitle: String,
    language: Language,
    onRename: ( String ) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var playlistName by remember { mutableStateOf( playlistTitle ) }

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
        },
        title = {
            Text( text = language.renamePlaylist )
        },
        text = {
            OutlinedTextField(
                value = playlistName,
                singleLine = true,
                onValueChange = {
                    playlistName = it
                },
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = playlistName.isNotEmpty() && playlistName != playlistTitle,
                onClick = {
                    onRename( playlistName )
                    onDismissRequest()
                }
            ) {
                Text( text = language.rename )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text( text = language.cancel )
            }
        }
    )
}

@Preview( showBackground = true )
@Composable
fun RenamePlaylistDialogPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    RenamePlaylistDialog(
        playlistTitle = previewData.playlists.first().title,
        language = English,
        onRename = { /*TODO*/ },
        onDismissRequest = {}
    )
}