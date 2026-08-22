package com.squad.musicmatters.feature.settings.nowPlaying

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squad.musicmatters.core.designsystem.R
import com.squad.musicmatters.feature.settings.components.SettingsOptionTile
import com.squad.musicmatters.feature.settings.components.SettingsSwitchTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun LyricsLayout(
    showLyricsOnSeparateScreen: Boolean,
    onShowLyricsOnSeparateScreenChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon(
                painter = painterResource( id = R.drawable.ic_lyrics,),
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = stringResource( id = i8nR.string.core_i8n_show_lyrics_on_separate_screen ),
                fontWeight = FontWeight.ExtraBold,
            )
        },
        value = showLyricsOnSeparateScreen,
        onChange = onShowLyricsOnSeparateScreenChange,
    )
}

