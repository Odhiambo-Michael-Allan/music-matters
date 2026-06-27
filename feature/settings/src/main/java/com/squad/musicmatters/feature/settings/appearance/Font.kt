package com.squad.musicmatters.feature.settings.appearance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.designsystem.theme.MusicMattersFont
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTypography
import com.squad.musicmatters.core.designsystem.theme.SYSTEM_DEFAULT_FONT_NAME
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.feature.settings.components.SettingsOptionTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun Font(
    font: MusicMattersFont,
    onFontChange: ( MusicMattersFont ) -> Unit
) {
    SettingsOptionTile(
        currentValue = font,
        possibleValues = MusicMattersTypography
            .all.values.associateBy(
                { it },
                {
                    if ( it.name == SYSTEM_DEFAULT_FONT_NAME )
                        stringResource( id = i8nR.string.core_i8n_system_default )
                    else it.name
                }
            ),
        enabled = true,
        dialogTitle = stringResource( id = i8nR.string.core_i8n_font ),
        onValueChange = onFontChange,
        leadingContentIcon = Icons.Filled.TextFormat,
        headlineContentText = stringResource( id = i8nR.string.core_i8n_font ),
        supportingContentText = font.name
    )
}
