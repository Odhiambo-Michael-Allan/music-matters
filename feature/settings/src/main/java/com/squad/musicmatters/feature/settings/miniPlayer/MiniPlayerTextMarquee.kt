package com.squad.musicmatters.feature.settings.miniPlayer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squad.musicmatters.feature.settings.components.SettingsSwitchTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun MiniPlayerTextMarquee(
    value: Boolean,
    onValueChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon(
                imageVector = Icons.Rounded.KeyboardDoubleArrowRight,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource( id = i8nR.string.core_i8n_mini_player_text_marquee ),
                fontWeight = FontWeight.ExtraBold,
            )
        },
        value = value,
        onChange = onValueChange
    )
}
