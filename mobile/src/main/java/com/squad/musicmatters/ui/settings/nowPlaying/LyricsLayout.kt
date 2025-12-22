package com.squad.musicmatters.ui.settings.nowPlaying

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.LyricsLayout
import com.squad.musicmatters.ui.settings.components.SettingsOptionTile

@Composable
fun LyricsLayout(
    nowPlayingLyricsLayout: LyricsLayout,
    language: Language,
    onNowPlayingLyricsLayoutChange: ( LyricsLayout ) -> Unit
) {
    SettingsOptionTile(
        currentValue = nowPlayingLyricsLayout,
        possibleValues = LyricsLayout.entries.toList().associateBy( { it }, { it.name } ),
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
        nowPlayingLyricsLayout = DefaultPreferences.LYRICS_LAYOUT,
        language = English,
        onNowPlayingLyricsLayoutChange = {}
    )
}