package com.odesa.musicMatters.ui.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsSwitchTile

@Composable
fun FadePlayback(
    language: Language,
    fadePlayback: Boolean,
    onFadePlaybackChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon(
                imageVector = Icons.Filled.GraphicEq,
                contentDescription = null
            )
        },
        title = { Text(text = language.fadePlaybackInOut ) },
        value = fadePlayback,
        onChange = onFadePlaybackChange
    )
}

@Preview( showBackground = true )
@Composable
fun FadePlaybackPreview() {
    FadePlayback(
        language = English,
        fadePlayback = true,
        onFadePlaybackChange = {}
    )
}