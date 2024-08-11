package com.odesa.musicMatters.ui.settings.nowPlaying

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsOptionTile

@Composable
fun ControlsLayout(
    controlsLayoutIsDefault: Boolean,
    language: Language,
    onNowPlayingControlsLayoutChange: ( Boolean ) -> Unit
) {
    val currentValue = if ( controlsLayoutIsDefault ) NowPlayingControlsLayout.Default else NowPlayingControlsLayout.Traditional
    SettingsOptionTile(
        currentValue = currentValue,
        possibleValues = NowPlayingControlsLayout.entries.toList().associateBy( { it }, { it.name } ),
        enabled = true,
        dialogTitle = language.controlsLayout,
        onValueChange = { if ( it == NowPlayingControlsLayout.Default ) onNowPlayingControlsLayoutChange( true ) else onNowPlayingControlsLayoutChange( false ) },
        leadingContentIcon = Icons.Filled.Dashboard,
        headlineContentText = language.controlsLayout,
        supportingContentText = if ( controlsLayoutIsDefault ) NowPlayingControlsLayout.Default.name else NowPlayingControlsLayout.Traditional.name
    )
}

@Preview( showBackground = true )
@Composable
fun ControlsLayoutPreview() {
    ControlsLayout(
        controlsLayoutIsDefault = SettingsDefaults.CONTROLS_LAYOUT_IS_DEFAULT,
        language = English,
        onNowPlayingControlsLayoutChange = {}
    )
}

enum class NowPlayingControlsLayout {
    Default, Traditional
}