package com.odesa.musicMatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsSwitchTile

@Composable
fun PauseOnHeadphonesDisconnect(
    language: Language,
    pauseOnHeadphonesDisconnect: Boolean,
    onPauseOnHeadphonesDisconnectChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = { Icon( imageVector = Icons.Filled.HeadsetOff, contentDescription = null ) },
        title = { Text( text = language.pauseOnHeadphonesDisconnect ) },
        value = pauseOnHeadphonesDisconnect,
        onChange = onPauseOnHeadphonesDisconnectChange
    )

}