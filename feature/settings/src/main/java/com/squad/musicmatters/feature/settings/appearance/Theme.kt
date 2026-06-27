package com.squad.musicmatters.feature.settings.appearance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.designsystem.theme.resolveName
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.feature.settings.components.SettingsOptionTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun Theme(
    themeMode: ThemeMode,
    onThemeChange: ( ThemeMode ) -> Unit
) {
    SettingsOptionTile(
        currentValue = themeMode,
        possibleValues = ThemeMode.entries.associateBy(
            { it },
            { it.resolveName() }
        ),
        enabled = true,
        dialogTitle = stringResource( id = i8nR.string.core_i8n_theme ),
        onValueChange = onThemeChange,
        leadingContentIcon = Icons.Filled.Palette,
        headlineContentText = stringResource( id = i8nR.string.core_i8n_theme ),
        supportingContentText = themeMode.resolveName()
    )
}
