package com.odesa.musicMatters.ui.settings.appearance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.odesa.musicMatters.core.designsystem.theme.MusicallyFont
import com.odesa.musicMatters.core.designsystem.theme.MusicallyTypography
import com.odesa.musicMatters.core.designsystem.theme.SupportedFonts
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.settings.components.SettingsOptionTile

@Composable
fun Font(
    font: MusicallyFont,
    language: Language,
    onFontChange: ( MusicallyFont ) -> Unit
) {
    SettingsOptionTile(
        currentValue = font,
        possibleValues = MusicallyTypography.all.values.associateBy( { it }, { it.name } ),
        enabled = true,
        dialogTitle = language.font,
        onValueChange = onFontChange,
        leadingContentIcon = Icons.Filled.TextFormat,
        headlineContentText = language.font,
        supportingContentText = font.name
    )
}


@Preview( showSystemUi = true )
@Composable
fun FontPreview() {
    Font(
        font = SupportedFonts.ProductSans,
        language = English,
        onFontChange = {}
    )
}