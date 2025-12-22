package com.squad.musicmatters.ui.settings.appearance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.designsystem.theme.resolveName
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.ui.settings.components.SettingsOptionTile

@Composable
fun Theme(
    language: Language,
    themeMode: ThemeMode,
    onThemeChange: ( ThemeMode ) -> Unit
) {
    SettingsOptionTile(
        currentValue = themeMode,
        possibleValues = ThemeMode.entries.associateBy({ it }, { it.resolveName(language) }),
        enabled = true,
        dialogTitle = language.theme,
        onValueChange = onThemeChange,
        leadingContentIcon = Icons.Filled.Palette,
        headlineContentText = language.theme,
        supportingContentText = themeMode.resolveName(language)
    )
}

@Preview( showSystemUi = true )
@Composable
fun ThemePreview() {
    Theme(
        language = English,
        themeMode = ThemeMode.FOLLOW_SYSTEM,
        onThemeChange = {}
    )
}