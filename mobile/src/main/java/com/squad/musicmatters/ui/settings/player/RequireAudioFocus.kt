package com.squad.musicmatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.squad.musicmatters.core.i8n.Language

import com.squad.musicmatters.ui.settings.components.SettingsSwitchTile

@Composable
fun RequireAudioFocus(
    language: Language,
    requireAudioFocus: Boolean,
    onRequireAudioFocusChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon(
                imageVector = Icons.Filled.CenterFocusWeak,
                contentDescription = null
            )
        },
        title = { Text( text = language.requireAudioFocus ) },
        value = requireAudioFocus,
        onChange = onRequireAudioFocusChange
    )

}