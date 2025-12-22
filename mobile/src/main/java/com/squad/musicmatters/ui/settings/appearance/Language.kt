package com.squad.musicmatters.ui.settings.appearance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.squad.musicmatters.core.i8n.Belarusian
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.i8n.SupportedLanguages
import com.squad.musicmatters.ui.settings.components.SettingsOptionTile

@Composable
fun Language(
    language: Language,
    onLanguageChange: ( Language ) -> Unit
) {
    SettingsOptionTile(
        currentValue = language,
        possibleValues = SupportedLanguages.associateBy( { it }, { it.nativeName } ),
        captions = SupportedLanguages.associateBy( { it }, { it.englishName } ),
        enabled = true,
        dialogTitle = language.language,
        onValueChange = onLanguageChange,
        leadingContentIcon = Icons.Filled.Language,
        headlineContentText = language.language,
        supportingContentText = language.nativeName
    )
}



@Preview( showSystemUi = true )
@Composable
fun LanguagePreview() {
    Language(
        language = Belarusian
    ) {}
}