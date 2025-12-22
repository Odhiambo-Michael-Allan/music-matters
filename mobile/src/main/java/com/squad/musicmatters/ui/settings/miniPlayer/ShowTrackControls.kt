package com.squad.musicmatters.ui.settings.miniPlayer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.ui.settings.components.SettingsSwitchTile

@Composable
fun ShowTrackControls(
    value: Boolean,
    language: Language,
    onValueChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = null
            )
        },
        title = { Text( text = language.showTrackControls) },
        value = value ,
        onChange = onValueChange
    )

}