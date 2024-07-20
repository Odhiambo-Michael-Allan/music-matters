package com.odesa.musicMatters.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Song

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
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = false
    ) {
        DeleteSongDialog(
            song = testSongs.first(),
            language = English,
            onDelete = { /*TODO*/ }
        ) {}
    }
}