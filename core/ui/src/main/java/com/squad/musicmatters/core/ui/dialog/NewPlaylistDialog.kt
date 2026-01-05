package com.squad.musicmatters.core.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.ThemeMode

@Composable
fun NewPlaylistDialog(
    songsToAdd: List<Song> = emptyList(),
    onConfirmation: ( String, List<Song> ) -> Unit,
    onDismissRequest: () -> Unit,
) {

    val placeholder = stringResource( id = R.string.core_i8n_playlist_name_placeholder )
    var playlistName by remember {
        mutableStateOf(
            TextFieldValue(
                text = placeholder,
                selection = TextRange( 0, placeholder.length )
            )
        )
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect( Unit ) { focusRequester.requestFocus() }

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.PlaylistAdd,
                contentDescription = null
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        title = {
            Text(
                text = stringResource( id = R.string.core_i8n_new_playlist),
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            OutlinedTextField(
                value = playlistName,
                singleLine = true,
                onValueChange = {
                    playlistName = it
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        playlistName.text.let {
                            if ( it.isNotEmpty() && it.isNotBlank() ) {
                                onConfirmation( playlistName.text, songsToAdd )
                            }
                        }
                        keyboardController?.hide()
                    }
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .focusRequester( focusRequester )
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = playlistName.text.isNotEmpty(),
                onClick = {
                    onConfirmation( playlistName.text, songsToAdd )
                }
            ) {
                Text(
                    text = stringResource( id = R.string.core_i8n_done ),
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            Row (
                modifier = Modifier.fillMaxWidth( 0.7f ),
                horizontalArrangement = Arrangement.Start,
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = stringResource( id = R.string.core_i8n_cancel ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    )
}

@Preview( showBackground = true )
@Composable
private fun NewPlaylistDialogPreview() {
    MusicMattersTheme(
        fontName = SupportedFonts.GoogleSans.name,
        useMaterialYou = true,
        fontScale = 1.0f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        NewPlaylistDialog(
            onConfirmation = { _, _ -> },
            onDismissRequest = {},
        )
    }

}