package com.odesa.musicMatters.ui.settings.miniPlayer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsSwitchTile

@Composable
fun ShowTrackControls(
    value: Boolean,
    language: Language,
    onValueChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = null
            )
        },
        title = { Text( text = language.showTrackControls) },
        value = value ,
        onChange = onValueChange
    )

}