package com.odesa.musicMatters.ui.settings.nowPlaying

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.odesa.musicMatters.core.data.preferences.NowPlayingLyricsLayout
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsOptionTile

@Composable
fun LyricsLayout(
    nowPlayingLyricsLayout: NowPlayingLyricsLayout,
    language: Language,
    onNowPlayingLyricsLayoutChange: (NowPlayingLyricsLayout) -> Unit
) {
    SettingsOptionTile(
        currentValue = nowPlayingLyricsLayout,
        possibleValues = NowPlayingLyricsLayout.entries.toList().associateBy( { it }, { it.name } ),
        enabled = true,
        dialogTitle = language.lyricsLayout,
        onValueChange = onNowPlayingLyricsLayoutChange,
        leadingContentIcon = Icons.Outlined.Article,
        headlineContentText = language.lyricsLayout,
        supportingContentText = nowPlayingLyricsLayout.name
    )
}

@Preview( showBackground = true )
@Composable
fun LyricsLayoutPreview() {
    LyricsLayout(
        nowPlayingLyricsLayout = SettingsDefaults.nowPlayingLyricsLayout,
        language = English,
        onNowPlayingLyricsLayoutChange = {}
    )
}