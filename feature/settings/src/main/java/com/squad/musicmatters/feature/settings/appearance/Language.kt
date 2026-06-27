package com.squad.musicmatters.feature.settings.appearance

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.feature.settings.components.SettingsOptionTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun Language(
) {
    val localeOptions = mapOf(
        "en" to i8nR.string.core_i8n_english,
        "fr" to i8nR.string.core_i8n_french,
        "hi" to i8nR.string.core_i8n_hindi,
        "ja" to i8nR.string.core_i8n_japanese,
        "pt-BR" to i8nR.string.core_i8n_portuguese_brazil,
        "pt-PT" to i8nR.string.core_i8n_portuguese_portugal,
    ).mapValues { stringResource( id = it.value ) }

    // Retrieve the currently configured app locale.
    // If no app-specific locale is set, LocaleListCompat.get(0) returns null,
    // so we safely fall back to a default (e.g., "en").
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val currentLocaleTag = appLocales.get(0)?.toLanguageTag() ?: "en"

    SettingsOptionTile(
        currentValue = currentLocaleTag,
        possibleValues = localeOptions,
        captions = null,
        enabled = true,
        dialogTitle = stringResource( id = i8nR.string.core_i8n_language ),
        onValueChange = { selectedLocale ->
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags( selectedLocale )
            )
        },
        leadingContentIcon = Icons.Filled.Language,
        headlineContentText = stringResource( id = i8nR.string.core_i8n_language ),
        supportingContentText = localeOptions[ currentLocaleTag ] ?: ""
    )
}



@Preview( showSystemUi = true )
@Composable
private fun LanguagePreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        Language()
    }
}