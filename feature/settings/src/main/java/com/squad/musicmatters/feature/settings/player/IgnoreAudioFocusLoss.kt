package com.squad.musicmatters.feature.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squad.musicmatters.feature.settings.components.SettingsSwitchTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun IgnoreAudioFocusLoss(
    ignoreAudioFocusLoss: Boolean,
    onIgnoreAudioFocusLossChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon( imageVector = Icons.Filled.CenterFocusWeak, contentDescription = null )
        },
        title = {
            Text(
                text = stringResource(
                    id = i8nR.string.core_i8n_ignore_audio_focus_loss
                ),
                fontWeight = FontWeight.ExtraBold,
            )
        },
        value = ignoreAudioFocusLoss,
        onChange = onIgnoreAudioFocusLossChange
    )
}