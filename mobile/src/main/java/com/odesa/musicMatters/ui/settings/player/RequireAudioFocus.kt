package com.odesa.musicMatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.odesa.musicMatters.core.i8n.Language

import com.odesa.musicMatters.ui.settings.components.SettingsSwitchTile

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