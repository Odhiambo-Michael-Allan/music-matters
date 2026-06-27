package com.squad.musicmatters.feature.settings.appearance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squad.musicmatters.feature.settings.components.SettingsSwitchTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun UseMaterialYou(
    useMaterialYou: Boolean,
    onUseMaterialYouChange: ( Boolean ) -> Unit
) {
    SettingsSwitchTile(
        icon = {
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource( id = i8nR.string.core_i8n_use_material_you ),
                fontWeight = FontWeight.ExtraBold,
            )
        },
        value = useMaterialYou,
        onChange = onUseMaterialYouChange
    )
}

