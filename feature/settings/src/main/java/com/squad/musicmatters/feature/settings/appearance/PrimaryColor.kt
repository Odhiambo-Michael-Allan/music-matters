package com.squad.musicmatters.feature.settings.appearance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.feature.settings.components.SettingsOptionTile
import com.squad.musicmatters.core.i8n.R as i8nR

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun PrimaryColor(
    primaryColor: String,
    onPrimaryColorChange: ( String ) -> Unit,
    useMaterialYou: Boolean
) {
    SettingsOptionTile(
        currentValue = primaryColor,
        possibleValues = PrimaryThemeColors.entries.toSet().associateBy(
            { it.name },
            { it.name }
        ),
        enabled = !useMaterialYou,
        dialogTitle = stringResource( id = i8nR.string.core_i8n_primary_color ),
        onValueChange = onPrimaryColorChange,
        leadingContentIcon = Icons.Filled.Colorize,
        headlineContentText = stringResource( id = i8nR.string.core_i8n_primary_color ),
        supportingContentText = primaryColor
    )
}
