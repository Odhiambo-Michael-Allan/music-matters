package com.squad.musicmatters.core.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.PreviewParameterData

@Composable
fun DeleteSongDialog(
    song: Song,
    language: Language,
    onDelete: () -> Unit,
    onDismissRequest: () -> Unit,
) {

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Outlined.DeleteForever,
                contentDescription = null
            )
        },
        title = {
            Text( text = "${language.delete}: ${song.title}" )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = true,
                onClick = {
                    onDelete()
                    onDismissRequest()
                }
            ) {
                Text( text = language.delete )
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
fun DeleteSongDialogPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = false
    ) {
        DeleteSongDialog(
            song = PreviewParameterData.songs.first(),
            language = English,
            onDelete = { /*TODO*/ }
        ) {}
    }
}