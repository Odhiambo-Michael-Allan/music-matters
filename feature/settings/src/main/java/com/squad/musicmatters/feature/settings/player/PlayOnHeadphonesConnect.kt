package com.squad.musicmatters.feature.settings.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squad.musicmatters.feature.settings.components.SettingsSwitchTile
import com.squad.musicmatters.core.i8n.R as i8nR


@Composable
fun PlayOnHeadphonesConnect(
    playOnHeadphonesConnect: Boolean,
    onPlayOnHeadphonesConnectChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = { Icon( imageVector = Icons.Filled.Headphones, contentDescription = null ) },
        title = {
            Text(
                text = stringResource(
                    id = i8nR.string.core_i8n_play_on_headphones_connect
                ),
                fontWeight = FontWeight.ExtraBold,
            )
        },
        value = playOnHeadphonesConnect,
        onChange = onPlayOnHeadphonesConnectChange
    )
}