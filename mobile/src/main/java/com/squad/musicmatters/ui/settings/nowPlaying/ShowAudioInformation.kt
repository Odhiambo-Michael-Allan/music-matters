package com.squad.musicmatters.ui.settings.nowPlaying

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wysiwyg
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.ui.settings.components.SettingsSwitchTile

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