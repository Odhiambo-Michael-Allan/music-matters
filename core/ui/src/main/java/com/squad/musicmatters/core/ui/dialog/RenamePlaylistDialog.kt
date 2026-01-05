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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.ui.PreviewParameterData

@Composable
fun RenamePlaylistDialog(
    playlistTitle: String,
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
            Text( text = stringResource( id = R.string.core_i8n_rename_playlist ) )
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
                Text( text = stringResource( id = R.string.core_i8n_rename ) )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text( text = stringResource( id = R.string.core_i8n_cancel ) )
            }
        }
    )
}

@Preview( showBackground = true )
@Composable
fun RenamePlaylistDialogPreview() {
    RenamePlaylistDialog(
        playlistTitle = PreviewParameterData.playlists.first().title,
        onRename = { /*TODO*/ },
        onDismissRequest = {}
    )
}