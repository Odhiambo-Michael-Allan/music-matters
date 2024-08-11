package com.odesa.musicMatters.ui.settings.nowPlaying

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wysiwyg
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsSwitchTile

@Composable
fun ShowAudioInformation(
    language: Language,
    value: Boolean,
    onValueChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = { 
            Icon(
                imageVector = Icons.Default.Wysiwyg,
                contentDescription = null
            )
        },
        title = {
            Text( text = language.showAudioInformation )
        },
        value = value,
        onChange = onValueChange
    )
}